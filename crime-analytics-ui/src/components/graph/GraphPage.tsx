import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Card, Input, Button, Space, Spin, message, Tag, Empty, Drawer, Tooltip, Dropdown } from 'antd';
import { SearchOutlined, ReloadOutlined, ZoomInOutlined, ZoomOutOutlined, ExpandOutlined, DownloadOutlined, FullscreenOutlined, NodeIndexOutlined, ClearOutlined, FilterOutlined } from '@ant-design/icons';
import apiClient from '../../api/apiClient';
import cytoscape from 'cytoscape';

const { Search } = Input;

const NODE_COLORS: Record<string, string> = {
  Case: '#1890ff',
  ACCUSED: '#ff4d4f',
  VICTIM: '#52c41a',
  COMPLAINANT: '#faad14',
  PERSON: '#722ed1',
};

const TYPE_ORDER = ['Case', 'ACCUSED', 'VICTIM', 'COMPLAINANT', 'PERSON'];

const LAYOUT_CONFIG = {
  name: 'cose',
  idealEdgeLength: 200,
  nodeRepulsion: 120000,
  gravity: 0.15,
  numIter: 4000,
  nodeOverlap: 50,
  padding: 50,
  randomize: true,
  nodeDimensionsIncludeLabels: true,
  componentSpacing: 200,
  refresh: 10,
};

const NODE_STYLE_BASE: Record<string, any> = {
  'background-color': 'data(color)',
  width: 'data(size)',
  height: 'data(size)',
  color: '#fff',
  'text-valign': 'bottom',
  'text-halign': 'center',
  'text-margin-y': 3,
  'font-size': '11px',
  'text-wrap': 'wrap',
  'text-max-width': '100px',
  'border-width': 2,
  'border-color': '#fff',
  'text-background-color': 'rgba(0,0,0,0.55)',
  'text-background-opacity': 1,
  'text-background-padding': '2px',
  'text-background-shape': 'roundrectangle',
};

const EDGE_STYLE_BASE: Record<string, any> = {
  width: 1.5,
  'line-color': 'data(color)',
  'target-arrow-color': 'data(color)',
  'target-arrow-shape': 'triangle',
  'arrow-scale': 1.2,
  'curve-style': 'unbundled-bezier',
  'control-point-distances': 10,
  'control-point-weights': 0.5,
  label: '',
  'font-size': '9px',
  color: '#555',
  'text-background-color': '#fff',
  'text-background-opacity': 0.85,
  'text-background-padding': '2px',
  'text-rotation': 'autorotate',
  'edge-distances': 'node-position',
};

const buildElements = (data: any) => {
  const nodeMap = new Map<string, any>();
  const edges: any[] = [];

  if (data.nodes && Array.isArray(data.nodes)) {
    data.nodes.forEach((n: any) => {
      const id = n.personId || n.crimeNo || n.id;
      if (!id || nodeMap.has(id)) return;
      const isCase = !!n.crimeNo && !n.personId;
      const type = isCase ? 'Case' : (n.personType || 'PERSON');
      const color = NODE_COLORS[type] || NODE_COLORS.PERSON;
      const size = isCase ? 26 : 20;
      const label = isCase ? n.crimeNo?.substring(0, 8) || id : (n.name || id);
      const extraProps: Record<string, any> = {};
      Object.entries(n).forEach(([k, v]) => {
        if (!['personId', 'crimeNo', 'id', 'personType', 'name'].includes(k)) {
          extraProps[k] = v;
        }
      });
      nodeMap.set(id, {
        group: 'nodes',
        data: { id, label, type, color, size, degree: 0, isCase, ...extraProps },
      });
    });
  }

  if (data.relationships && Array.isArray(data.relationships)) {
    data.relationships.forEach((r: any, idx: number) => {
      const src = r.fromId || r.personId;
      const tgt = r.toId || r.crimeNo;
      if (src && tgt && nodeMap.has(src) && nodeMap.has(tgt)) {
        const relLabel = r.type || r.relType || '';
        const relColor = relLabel === 'CO_OFFENDER' ? '#ff4d4f' : (relLabel === 'ARRESTS' ? '#e67e22' : '#999');
        nodeMap.get(src).data.degree = (nodeMap.get(src).data.degree || 0) + 1;
        nodeMap.get(tgt).data.degree = (nodeMap.get(tgt).data.degree || 0) + 1;
        const extraProps: Record<string, any> = {};
        Object.entries(r).forEach(([k, v]) => {
          if (!['fromId', 'personId', 'toId', 'crimeNo', 'type', 'relType'].includes(k)) {
            extraProps[k] = v;
          }
        });
        edges.push({
          group: 'edges',
          data: { id: `e${idx}`, source: src, target: tgt, label: relLabel, color: relColor, ...extraProps },
        });
      }
    });
  }

  const nodes = Array.from(nodeMap.values());
  return { nodes, edges };
};


export const GraphPage: React.FC = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const cyRef = useRef<any>(null);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState<any>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [graphData, setGraphData] = useState<any>(null);
  const [hasData, setHasData] = useState(false);
  const [selectedElement, setSelectedElement] = useState<any>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [typeVisibility, setTypeVisibility] = useState<Record<string, boolean>>(
    Object.fromEntries(TYPE_ORDER.map(k => [k, true]))
  );
  const [highlightId, setHighlightId] = useState<string | null>(null);
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [activeTypes, setActiveTypes] = useState<string[]>(TYPE_ORDER);
  const [tooltipState, setTooltipState] = useState<{ x: number; y: number; text: string; type: string } | null>(null);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => { mountedRef.current = false; };
  }, []);

  const updateEdgeLabels = useCallback((cy: any) => {
    const zoom = cy.zoom();
    cy.batch(() => {
      if (zoom < 1.0) {
        cy.edges().style('label', '');
      } else {
        cy.edges().forEach((e: any) => {
          e.style('label', e.data('label'));
        });
      }
    });
  }, []);

  const initCy = useCallback(() => {
    if (cyRef.current) return cyRef.current;
    if (!containerRef.current) return null;
    const cy = cytoscape({
      container: containerRef.current,
      style: [
        { selector: 'node', style: NODE_STYLE_BASE },
        { selector: 'edge', style: EDGE_STYLE_BASE },
        { selector: 'node:selected', style: { 'border-width': 4, 'border-color': '#ffd700' } },
        { selector: 'edge:selected', style: { width: 3, 'line-color': '#ffd700' } },
        {
          selector: 'node.highlighted',
          style: {
            'border-width': 6,
            'border-color': '#ffd700',
            'border-opacity': 1,
          },
        },
        {
          selector: 'node.dimmed',
          style: { opacity: 0.15, 'text-opacity': 0.15 },
        },
        {
          selector: 'edge.dimmed',
          style: { opacity: 0.1, 'text-opacity': 0.1 },
        },
        {
          selector: 'node.neighbor',
          style: {
            'border-width': 3,
            'border-color': '#ffd700',
            'border-opacity': 0.6,
          },
        },
        {
          selector: 'edge.neighbor',
          style: {
            width: 2.5,
            'line-color': '#ffd700',
            opacity: 0.7,
          },
        },
      ],
      layout: { name: 'grid' },
      minZoom: 0.15,
      maxZoom: 4,
    });

    cy.on('zoom', () => { updateEdgeLabels(cy); });
    cy.on('mouseover', 'node', (evt: any) => {
      evt.target.style('font-size', '12px');
      const rp = evt.target.renderedPosition();
      setTooltipState({ x: rp.x, y: rp.y - 10, text: evt.target.data('label'), type: evt.target.data('type') });
    });
    cy.on('mouseout', 'node', (evt: any) => {
      evt.target.style('font-size', '10px');
      setTooltipState(null);
    });
    cy.on('mouseover', 'edge', (evt: any) => {
      evt.target.style('label', evt.target.data('label'));
      evt.target.style('font-size', '10px');
    });
    cy.on('mouseout', 'edge', (evt: any) => {
      const zoom = cy.zoom();
      if (zoom < 1.0) {
        evt.target.style('label', '');
      }
      evt.target.style('font-size', '9px');
    });

    cyRef.current = cy;
    return cy;
  }, [updateEdgeLabels]);

  const applyHighlight = useCallback((cy: any, nodeId: string | null) => {
    if (!nodeId) {
      cy.batch(() => {
        cy.elements().removeClass('dimmed neighbor highlighted');
        cy.nodes().style('opacity', 1);
        cy.edges().style('opacity', 1);
      });
      return;
    }
    const target = cy.getElementById(nodeId);
    if (!target || target.length === 0) return;
    const neighborIds = new Set<string>();
    neighborIds.add(nodeId);
    target.neighborhood().nodes().forEach((n: any) => neighborIds.add(n.id()));
    cy.batch(() => {
      cy.nodes().forEach((n: any) => {
        if (neighborIds.has(n.id())) {
          n.removeClass('dimmed').addClass('neighbor');
          n.style('opacity', 1);
        } else {
          n.removeClass('neighbor').addClass('dimmed');
          n.style('opacity', 0.15);
        }
      });
      cy.edges().forEach((e: any) => {
        const src = e.data('source');
        const tgt = e.data('target');
        if (neighborIds.has(src) && neighborIds.has(tgt)) {
          e.removeClass('dimmed').addClass('neighbor');
          e.style('opacity', 0.7);
        } else {
          e.removeClass('neighbor').addClass('dimmed');
          e.style('opacity', 0.1);
        }
      });
      target.removeClass('neighbor').addClass('highlighted');
    });
  }, []);

  const resolveCollisions = useCallback((cy: any, buffer: number) => {
    const visible = cy.nodes(':visible');
    if (visible.length < 2) return;
    for (let iter = 0; iter < 15; iter++) {
      let moved = false;
      for (let i = 0; i < visible.length; i++) {
        for (let j = i + 1; j < visible.length; j++) {
          const ni = visible[i];
          const nj = visible[j];
          const pi = ni.position();
          const pj = nj.position();
          const ri = ni.width() / 2;
          const rj = nj.width() / 2;
          const dx = pj.x - pi.x;
          const dy = pj.y - pi.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const minDist = ri + rj + buffer;
          if (dist < minDist && dist > 0.01) {
            const overlap = minDist - dist;
            const angle = Math.atan2(dy, dx);
            const push = overlap * 0.5;
            ni.position({ x: pi.x - push * Math.cos(angle), y: pi.y - push * Math.sin(angle) });
            nj.position({ x: pj.x + push * Math.cos(angle), y: pj.y + push * Math.sin(angle) });
            moved = true;
          }
        }
      }
      if (!moved) break;
    }
  }, []);

  const applyTypeFilters = useCallback((cy: any) => {
    const hiddenTypes = Object.entries(typeVisibility).filter(([, v]) => !v).map(([k]) => k);
    if (hiddenTypes.length === 0) {
      cy.elements().show();
      return;
    }
    cy.batch(() => {
      cy.nodes().forEach((n: any) => {
        const t = n.data('type');
        if (hiddenTypes.includes(t)) {
          n.hide();
        } else {
          n.show();
        }
      });
      cy.edges().forEach((e: any) => {
        const src = e.source();
        const tgt = e.target();
        if (src && tgt && src.visible() && tgt.visible()) {
          e.show();
        } else {
          e.hide();
        }
      });
    });
  }, [typeVisibility]);

  const applyTypeFiltersRef = useRef(applyTypeFilters);
  applyTypeFiltersRef.current = applyTypeFilters;

  const updateGraph = useCallback((elements: any) => {
    const cy = cyRef.current;
    if (!cy || !mountedRef.current) return;

    if (!elements || elements.nodes.length === 0) {
      cy.elements().remove();
      setHasData(false);
      setActiveTypes([]);
      setDetailVisible(false);
      return;
    }

    setHighlightId(null);
    cy.elements().remove();
    cy.add(elements);
    setHasData(true);
    applyTypeFiltersRef.current(cy);

    const layout = cy.layout(LAYOUT_CONFIG);
    layout.one('layoutstop', () => {
      if (!mountedRef.current) return;
      const presentTypes = [...new Set(cy.nodes(':visible').map((n: any) => n.data('type')))];
      setActiveTypes(TYPE_ORDER.filter(t => presentTypes.includes(t)));
      cy.nodes().forEach((n: any) => {
        const deg = n.data('degree') || 0;
        const baseSize = n.data('isCase') ? 26 : 20;
        const extra = Math.min(deg * 2, 10);
        n.style('width', baseSize + extra);
        n.style('height', baseSize + extra);
      });
      resolveCollisions(cy, 20);
      updateEdgeLabels(cy);
    });
    layout.run();
  }, [updateEdgeLabels]);

  useEffect(() => {
    if (!graphData) return;
    const cy = cyRef.current || initCy();
    if (cy) {
      const elements = buildElements(graphData);
      updateGraph(elements);
    }
  }, [graphData]); // eslint-disable-line react-hooks/exhaustive-deps

  const loadAll = useCallback(async () => {
    if (!mountedRef.current) return;
    setLoading(true);
    setHighlightId(null);
    setDetailVisible(false);
    try {
      const [statsRes, fullRes] = await Promise.all([
        apiClient.get('/api/v1/graph/stats'),
        apiClient.get('/api/v1/graph/full'),
      ]);
      if (!mountedRef.current) return;
      setStats(statsRes.data);
      setGraphData(fullRes.data);
    } catch (err: any) {
      if (!mountedRef.current) return;
      message.error('Failed to load graph: ' + (err.response?.data?.message || err.message));
      setHasData(false);
      setActiveTypes([]);
    } finally {
      if (mountedRef.current) setLoading(false);
    }
  }, []);

  const loadPersonNetwork = useCallback(async (personId: string) => {
    if (!mountedRef.current) return;
    setLoading(true);
    setHighlightId(null);
    setDetailVisible(false);
    try {
      const res = await apiClient.get(`/api/v1/graph/person/${personId}/network`);
      if (!mountedRef.current) return;
      setGraphData(res.data);
    } catch (err) {
      if (!mountedRef.current) return;
      message.error('Failed to load network');
    } finally {
      if (mountedRef.current) setLoading(false);
    }
  }, []);

  const handleNodeClick = useCallback((evt: any) => {
    const nd = evt.target;
    const cy = nd.cy();
    const data = nd.data();
    const id = data.id;

    setSelectedNodeId(id);
    setHighlightId(id);
    applyHighlight(cy, id);

    const connectedEdges = nd.connectedEdges();
    const connectedNodes = nd.neighborhood().nodes();
    const caseIds: string[] = [];
    const directConnections: { label: string; type: string; rel: string }[] = [];

    connectedEdges.forEach((e: any) => {
      const src = e.data('source');
      const tgt = e.data('target');
      const neighborId = src === id ? tgt : src;
      const neighbor = cy.getElementById(neighborId);
      if (neighbor.length > 0) {
        const nData = neighbor.data();
        const relLabel = e.data('label') || '';
        if (nData.type === 'Case') {
          caseIds.push(nData.label || neighborId);
        }
        directConnections.push({
          label: nData.label || nData.id || neighborId,
          type: nData.type || '',
          rel: relLabel,
        });
      }
    });

    const detailData: any = { type: 'node', nodeType: data.type, id, label: data.label };
    detailData.sections = [];
    if (data.name) {
      detailData.sections.push({ title: 'Full Name', value: data.name });
    } else if (data.label) {
      detailData.sections.push({ title: 'Label', value: data.label });
    }
    if (data.personId) detailData.sections.push({ title: 'Person ID', value: data.personId });
    if (data.crimeNo) detailData.sections.push({ title: 'FIR Number', value: data.crimeNo });
    if (data.phone) detailData.sections.push({ title: 'Phone', value: data.phone });
    if (data.address) detailData.sections.push({ title: 'Address', value: data.address });
    if (data.firDate) detailData.sections.push({ title: 'FIR Date', value: data.firDate });
    if (data.crimeHeadName) detailData.sections.push({ title: 'Crime Head', value: data.crimeHeadName });
    if (data.districtName) detailData.sections.push({ title: 'District', value: data.districtName });
    if (data.unitName) detailData.sections.push({ title: 'Unit', value: data.unitName });
    if (data.degree !== undefined) detailData.sections.push({ title: 'Connections', value: String(data.degree) });

    detailData.cases = [...new Set(caseIds)];
    detailData.connections = directConnections;
    setSelectedElement(detailData);
    setDetailVisible(true);
  }, []);

  const handleEdgeClick = useCallback((evt: any) => {
    const ed = evt.target;
    const data = ed.data();
    const srcData = ed.source().data();
    const tgtData = ed.target().data();
    setSelectedElement({
      type: 'edge',
      id: data.id,
      label: data.label || 'Relationship',
      nodeType: null,
      sections: [
        { key: 'rel', title: 'Relationship', value: data.label || '—' },
        { key: 'fromName', title: 'From', value: srcData.label || srcData.id },
        { key: 'fromRole', title: 'From Role', value: srcData.type || '—' },
        { key: 'toName', title: 'To', value: tgtData.label || tgtData.id },
        { key: 'toRole', title: 'To Role', value: tgtData.type || '—' },
      ],
    });
    setDetailVisible(true);
  }, []);

  const handleBackgroundClick = useCallback((evt: any) => {
    if (evt.target === evt.cy) {
      setDetailVisible(false);
      setSelectedElement(null);
      setSelectedNodeId(null);
      setHighlightId(null);
      const cy = evt.cy;
      applyHighlight(cy, null);
    }
  }, []);

  useEffect(() => {
    const cy = cyRef.current;
    if (!cy) return;
    cy.on('tap', 'node', handleNodeClick);
    cy.on('tap', 'edge', handleEdgeClick);
    cy.on('tap', handleBackgroundClick);
    return () => {
      try {
        cy.removeListener('tap', 'node', handleNodeClick);
        cy.removeListener('tap', 'edge', handleEdgeClick);
        cy.removeListener('tap', handleBackgroundClick);
      } catch (e) {}
    };
  }, [handleNodeClick, handleEdgeClick, handleBackgroundClick, graphData]);

  const populateGraph = async () => {
    setLoading(true);
    try {
      await apiClient.post('/api/v1/graph/populate');
      message.success('Graph populated from database');
      loadAll();
    } catch (err: any) {
      message.error('Failed to populate: ' + (err.response?.data?.message || err.message));
      setLoading(false);
    }
  };

  const searchPerson = async () => {
    if (!searchQuery.trim()) { loadAll(); return; }
    const cy = cyRef.current;
    if (cy) {
      const match = cy.getElementById(searchQuery.trim());
      if (match && match.length > 0) {
        setHighlightId(searchQuery.trim());
        applyHighlight(cy, searchQuery.trim());
        return;
      }
      const fuzzy = cy.nodes().filter((n: any) =>
        String(n.data('label') || '').toLowerCase().includes(searchQuery.trim().toLowerCase())
      );
      if (fuzzy.length > 0) {
        const id = fuzzy[0].id();
        setHighlightId(id);
        applyHighlight(cy, id);
        return;
      }
    }
    setLoading(true);
    try {
      const res = await apiClient.get('/api/v1/graph/search', { params: { q: searchQuery } });
      const persons = res.data.results || [];
      if (persons.length > 0) {
        loadPersonNetwork(persons[0].personId);
      } else {
        message.info('No persons found');
        setHasData(false);
        setLoading(false);
      }
    } catch (err) {
      message.error('Search failed');
      setLoading(false);
    }
  };

  const zoomIn = () => {
    const cy = cyRef.current;
    if (cy) { try { cy.zoom(cy.zoom() * 1.4); cy.center(); } catch (e) {} }
  };
  const zoomOut = () => {
    const cy = cyRef.current;
    if (cy) { try { cy.zoom(cy.zoom() / 1.4); } catch (e) {} }
  };
  const fitGraph = () => {
    const cy = cyRef.current;
    if (cy) { try { cy.fit(undefined, 30); } catch (e) {} }
  };
  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
    } else {
      document.exitFullscreen();
    }
  };
  const exportPNG = () => {
    const cy = cyRef.current;
    if (!cy) return;
    const blob = cy.png({ output: 'blob', full: true, scale: 2 });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'criminal-network.png';
    a.click();
    URL.revokeObjectURL(url);
  };
  const exportJSON = () => {
    const cy = cyRef.current;
    if (!cy) return;
    const json = cy.json();
    const blob = new Blob([JSON.stringify(json, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'criminal-network.json';
    a.click();
    URL.revokeObjectURL(url);
  };
  const clearHighlight = () => {
    const cy = cyRef.current;
    setHighlightId(null);
    setSelectedNodeId(null);
    if (cy) applyHighlight(cy, null);
  };

  const toggleTypeVisibility = (type: string) => {
    setTypeVisibility(prev => {
      const next = { ...prev, [type]: !prev[type] };
      return next;
    });
  };

  useEffect(() => {
    const cy = cyRef.current;
    if (cy) applyTypeFilters(cy);
  }, [typeVisibility, applyTypeFilters]);

  useEffect(() => {
    const cy = cyRef.current;
    if (cy && highlightId) applyHighlight(cy, highlightId);
    else if (cy) applyHighlight(cy, null);
  }, [highlightId, applyHighlight]);

  useEffect(() => {
    loadAll();
    return () => {
      if (cyRef.current) {
        try { cyRef.current.destroy(); } catch (e) {}
        cyRef.current = null;
      }
    };
  }, [loadAll]);

  const totalNodes = stats?.nodes?.reduce((sum: number, n: any) => sum + n.count, 0) || 0;
  const totalRels = stats?.relationships?.reduce((sum: number, r: any) => sum + r.count, 0) || 0;

  return (
    <div style={{ padding: 12, height: 'calc(100vh - 64px)', display: 'flex', flexDirection: 'column' }}>
      <Card size="small" style={{ marginBottom: 8, borderRadius: 8 }} styles={{ body: { padding: '8px 16px' } }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 8 }}>
          <Space wrap align="center">
            <span style={{ fontWeight: 600, fontSize: 15, marginRight: 8 }}>Criminal Network</span>
            <Search
              placeholder="Search name in graph..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              onSearch={searchPerson}
              style={{ width: 240 }}
              prefix={<SearchOutlined />}
              size="small"
            />
            {highlightId && (
              <Button icon={<ClearOutlined />} size="small" onClick={clearHighlight}>
                Clear
              </Button>
            )}
          </Space>
          <Space wrap>
            {stats && (
              <Space size="middle" style={{ marginRight: 8 }}>
                <Tag color="blue">{totalNodes} Nodes</Tag>
                <Tag color="green">{totalRels} Rel.</Tag>
              </Space>
            )}
            <Button icon={<FilterOutlined />} size="small" onClick={populateGraph} type="primary">
              Populate
            </Button>
            <Button icon={<ReloadOutlined />} size="small" onClick={loadAll} loading={loading}>
              Refresh
            </Button>
          </Space>
        </div>
      </Card>

      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        {loading && (
          <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', zIndex: 10 }}>
            <Spin size="large" />
          </div>
        )}
        <div
          ref={containerRef}
          style={{ width: '100%', height: '100%', borderRadius: 8, background: '#fafafa' }}
        />
        {tooltipState && (
          <div style={{
            position: 'absolute',
            left: tooltipState.x,
            top: tooltipState.y,
            transform: 'translate(-50%, -100%)',
            background: 'rgba(0,0,0,0.8)',
            color: '#fff',
            fontSize: 12,
            padding: '4px 10px',
            borderRadius: 6,
            pointerEvents: 'none',
            zIndex: 20,
            whiteSpace: 'nowrap',
            display: 'flex',
            alignItems: 'center',
            gap: 6,
          }}>
            <span>{tooltipState.text}</span>
            <span style={{
              background: NODE_COLORS[tooltipState.type] || '#999',
              borderRadius: 4,
              padding: '1px 5px',
              fontSize: 9,
              fontWeight: 600,
              textTransform: 'uppercase',
            }}>
              {tooltipState.type}
            </span>
          </div>
        )}
        {!loading && !hasData && (
          <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }}>
            <Empty description="No graph data. Click Populate to load." />
          </div>
        )}

        {hasData && (
          <div style={{ position: 'absolute', top: 10, left: 10, display: 'flex', flexDirection: 'column', gap: 4, zIndex: 5 }}>
            <Tooltip title="Zoom In"><Button icon={<ZoomInOutlined />} size="small" onClick={zoomIn} style={{ opacity: 0.8 }} /></Tooltip>
            <Tooltip title="Zoom Out"><Button icon={<ZoomOutOutlined />} size="small" onClick={zoomOut} style={{ opacity: 0.8 }} /></Tooltip>
            <Tooltip title="Fit to Canvas"><Button icon={<ExpandOutlined />} size="small" onClick={fitGraph} style={{ opacity: 0.8 }} /></Tooltip>
            <Dropdown menu={{ items: [
              { key: 'png', icon: <DownloadOutlined />, label: 'Download PNG', onClick: exportPNG },
              { key: 'json', icon: <DownloadOutlined />, label: 'Export JSON', onClick: exportJSON },
            ]}} trigger={['click']}>
              <Tooltip title="Export"><Button icon={<DownloadOutlined />} size="small" style={{ opacity: 0.8 }} /></Tooltip>
            </Dropdown>
            <Tooltip title="Fullscreen"><Button icon={<FullscreenOutlined />} size="small" onClick={toggleFullscreen} style={{ opacity: 0.8 }} /></Tooltip>
          </div>
        )}

        {hasData && (
          <div style={{ position: 'absolute', bottom: 10, left: '50%', transform: 'translateX(-50%)', background: 'rgba(255,255,255,0.92)', borderRadius: 8, padding: '4px 12px', boxShadow: '0 1px 4px rgba(0,0,0,0.12)', zIndex: 5, display: 'flex', alignItems: 'center', gap: 6 }}>
            <span style={{ fontSize: 11, color: '#888', marginRight: 4, whiteSpace: 'nowrap' }}>Show:</span>
            {activeTypes.map((type: string) => {
              const color = NODE_COLORS[type];
              const visible = typeVisibility[type];
              return (
                <Tag
                  key={type}
                  color={visible ? color : undefined}
                  style={{
                    cursor: 'pointer',
                    fontSize: 11,
                    lineHeight: '18px',
                    padding: '0 6px',
                    opacity: visible ? 1 : 0.35,
                    userSelect: 'none',
                    border: visible ? undefined : `1px solid #d9d9d9`,
                  }}
                  onClick={() => toggleTypeVisibility(type)}
                >
                  {type}
                </Tag>
              );
            })}
          </div>
        )}
      </div>

      <Drawer
        title={
          selectedElement ? (
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Space>
                {selectedElement.nodeType && (
                  <div style={{ width: 12, height: 12, borderRadius: '50%', background: NODE_COLORS[selectedElement.nodeType] || '#999', display: 'inline-block' }} />
                )}
                <span>{selectedElement.label || 'Details'}</span>
                {selectedElement.nodeType && (
                  <Tag color={NODE_COLORS[selectedElement.nodeType]} style={{ fontSize: 10, lineHeight: '16px', padding: '0 4px' }}>
                    {selectedElement.nodeType}
                  </Tag>
                )}
              </Space>
            </div>
          ) : 'Details'
        }
        placement="right"
        onClose={() => {
          setDetailVisible(false);
          if (selectedNodeId) {
            setSelectedNodeId(null);
            setHighlightId(null);
            const cy = cyRef.current;
            if (cy) applyHighlight(cy, null);
          }
        }}
        open={detailVisible}
        width={340}
        styles={{ body: { padding: '16px 20px', display: 'flex', flexDirection: 'column', height: 'calc(100% - 55px)' } }}
        footer={
          selectedElement && selectedElement.nodeType ? (
            <div style={{ padding: '8px 0', display: 'flex', flexDirection: 'column', gap: 6 }}>
              <Button
                type="primary"
                block
                size="small"
                icon={<ExpandOutlined />}
                onClick={() => {
                  const cy = cyRef.current;
                  if (cy && selectedNodeId) {
                    const target = cy.getElementById(selectedNodeId);
                    if (target && target.length > 0) {
                      cy.fit(target.neighborhood().add(target), 50);
                    }
                  }
                }}
              >
                Focus on Graph
              </Button>
              {selectedElement.nodeType && selectedElement.nodeType !== 'Case' && (
                <Button block size="small" icon={<NodeIndexOutlined />} onClick={() => {
                  loadPersonNetwork(selectedElement.id);
                  setDetailVisible(false);
                }}>
                  Expand Network
                </Button>
              )}
            </div>
          ) : null
        }
      >
        {selectedElement ? (
          <div style={{ flex: 1, overflowY: 'auto' }}>
            {selectedElement.type === 'node' && (
              <>
                {selectedElement.sections.map((s: any) => (
                  <div key={s.title} style={{ marginBottom: 10 }}>
                    <div style={{ fontSize: 10, color: '#888', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 1 }}>{s.title}</div>
                    <div style={{ fontSize: 13, color: '#222', wordBreak: 'break-word' }}>{s.value}</div>
                  </div>
                ))}
                {selectedElement.cases && selectedElement.cases.length > 0 && (
                  <div style={{ marginBottom: 10 }}>
                    <div style={{ fontSize: 10, color: '#888', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 4 }}>Associated Cases</div>
                    {selectedElement.cases.map((c: string) => (
                      <Tag key={c} color={NODE_COLORS.Case} style={{ fontSize: 11, marginBottom: 2 }}>{c}</Tag>
                    ))}
                  </div>
                )}
                {selectedElement.connections && selectedElement.connections.length > 0 && (
                  <div style={{ marginBottom: 10 }}>
                    <div style={{ fontSize: 10, color: '#888', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 4 }}>Direct Connections ({selectedElement.connections.length})</div>
                    {selectedElement.connections.map((c: any, i: number) => (
                      <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 3, fontSize: 12 }}>
                        <div style={{ width: 8, height: 8, borderRadius: '50%', background: NODE_COLORS[c.type] || '#999', flexShrink: 0 }} />
                        <span style={{ flex: 1, color: '#333' }}>{c.label}</span>
                        <Tag style={{ fontSize: 9, lineHeight: '14px', padding: '0 3px', margin: 0, border: '1px solid #d9d9d9' }}>{c.rel || c.type}</Tag>
                      </div>
                    ))}
                  </div>
                )}
              </>
            )}
            {selectedElement.type === 'edge' && (
              <>
                {selectedElement.sections.map((s: any) => (
                  <div key={s.title} style={{ marginBottom: 10 }}>
                    <div style={{ fontSize: 10, color: '#888', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 1 }}>{s.title}</div>
                    <div style={{ fontSize: 13, color: '#222', wordBreak: 'break-word' }}>{s.value}</div>
                  </div>
                ))}
              </>
            )}
          </div>
        ) : (
          <div style={{ color: '#999', textAlign: 'center', marginTop: 40 }}>
            Click a node or edge to see details
          </div>
        )}
      </Drawer>
    </div>
  );
};

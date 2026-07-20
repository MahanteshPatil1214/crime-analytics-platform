import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Spin, Tag } from 'antd';
import {
  SendOutlined,
  RobotOutlined,
  UserOutlined,
  FileTextOutlined,
  BarChartOutlined,
  DollarOutlined,
  TeamOutlined,
  SearchOutlined,
  ClearOutlined,
  DownloadOutlined,
  FolderOpenOutlined,
  PieChartOutlined,
  CheckCircleOutlined,
  LinkOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { chatApi, ChatResponse, Suggestion } from '../../api/chatApi';
import { useChatStore } from '../../stores/chatStore';
import { ChatMessage } from '../../types/chat';

const ICON_MAP: Record<string, React.ReactNode> = {
  'folder-open': <FolderOpenOutlined />,
  'bar-chart': <BarChartOutlined />,
  'file-pdf': <FileTextOutlined />,
  'search': <SearchOutlined />,
  'dollar': <DollarOutlined />,
  'user-delete': <TeamOutlined />,
  'check-circle': <CheckCircleOutlined />,
  'pie-chart': <PieChartOutlined />,
};

const CATEGORY_COLORS: Record<string, string> = {
  Cases: '#1890ff',
  Analytics: '#722ed1',
  Reports: '#ff4d4f',
  Search: '#52c41a',
  Financial: '#faad14',
  Persons: '#13c2c2',
};

function renderMarkdownLine(text: string, key: string, navigate: ReturnType<typeof useNavigate>): React.ReactNode {
  const tokens = text.split(/(\*\*[^*]+\*\*|\[[^\]]+\]\([^)]+\)|`[^`]+`|_[^_]+_)/g);
  return (
    <React.Fragment key={key}>
      {tokens.map((token, i) => {
        if (token.startsWith('**') && token.endsWith('**')) {
          const inner = token.slice(2, -2);
          const linkMatch = inner.match(/^\[([^\]]+)\]\(([^)]+)\)$/);
          if (linkMatch) {
            return (
              <a key={i} href={linkMatch[2]} onClick={(e) => { e.preventDefault(); navigate(linkMatch[2]); }}
                style={{ color: '#1890ff', fontWeight: 700, cursor: 'pointer', textDecoration: 'underline' }}>
                {linkMatch[1]}
              </a>
            );
          }
          return <strong key={i} style={{ color: '#1a1a1a' }}>{inner}</strong>;
        }
        if (token.startsWith('[') && token.includes('](')) {
          const m = token.match(/^\[([^\]]+)\]\(([^)]+)\)$/);
          if (m) {
            return (
              <a key={i} href={m[2]} onClick={(e) => { e.preventDefault(); navigate(m[2]); }}
                style={{ color: '#1890ff', cursor: 'pointer', textDecoration: 'underline' }}>
                {m[1]}
              </a>
            );
          }
        }
        if (token.startsWith('`') && token.endsWith('`')) {
          return (
            <code key={i} style={{
              background: '#f0f0f0', padding: '1px 5px', borderRadius: 3,
              fontSize: 13, fontFamily: 'monospace', color: '#c41d7f',
            }}>
              {token.slice(1, -1)}
            </code>
          );
        }
        if (token.startsWith('_') && token.endsWith('_') && token.length > 2) {
          return <em key={i} style={{ color: '#666' }}>{token.slice(1, -1)}</em>;
        }
        return <span key={i}>{token}</span>;
      })}
    </React.Fragment>
  );
}

function renderMarkdown(text: string, navigate: ReturnType<typeof useNavigate>): React.ReactNode[] {
  const lines = text.split('\n');
  const elements: React.ReactNode[] = [];
  let inTable = false;
  let tableRows: string[][] = [];
  let tableHeaders: string[] = [];

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];

    if (line.startsWith('|') && line.endsWith('|')) {
      const cells = line.split('|').filter((c) => c.trim() !== '').map((c) => c.trim());
      if (cells.every((c) => c.match(/^[-:]+$/))) continue;
      if (!inTable) { inTable = true; tableHeaders = cells; tableRows = []; }
      else tableRows.push(cells);
      continue;
    }

    if (inTable) {
      elements.push(renderTable(tableHeaders, tableRows, i));
      inTable = false; tableRows = []; tableHeaders = [];
    }

    if (line.startsWith('> ')) {
      elements.push(
        <div key={i} style={{
          borderLeft: '3px solid #d9d9d9', paddingLeft: 12, color: '#555',
          fontStyle: 'italic', margin: '4px 0', fontSize: 13,
        }}>
          {renderMarkdownLine(line.substring(2), String(i), navigate)}
        </div>
      );
    } else if (line.startsWith('**') && line.endsWith('**') && !line.includes('](')) {
      elements.push(
        <div key={i} style={{ fontWeight: 700, fontSize: 15, marginTop: 12, marginBottom: 4, color: '#1a1a1a' }}>
          {line.replace(/\*\*/g, '')}
        </div>
      );
    } else if (line.startsWith('- ')) {
      elements.push(
        <div key={i} style={{ paddingLeft: 16, marginBottom: 3, lineHeight: 1.6 }}>
          <span style={{ color: '#999', marginRight: 8 }}>•</span>
          {renderMarkdownLine(line.substring(2), String(i), navigate)}
        </div>
      );
    } else if (line.trim() === '') {
      elements.push(<div key={i} style={{ height: 8 }} />);
    } else {
      elements.push(
        <div key={i} style={{ marginBottom: 3, lineHeight: 1.6 }}>
          {renderMarkdownLine(line, String(i), navigate)}
        </div>
      );
    }
  }

  if (inTable) elements.push(renderTable(tableHeaders, tableRows, lines.length));
  return elements;
}

function renderTable(headers: string[], rows: string[][], key: number): React.ReactNode {
  return (
    <div key={`table-${key}`} style={{
      overflowX: 'auto', margin: '8px 0', borderRadius: 6,
      border: '1px solid #e8e8e8', fontSize: 13,
    }}>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ background: '#fafafa' }}>
            {headers.map((h, i) => (
              <th key={i} style={{
                padding: '6px 10px', textAlign: 'left', borderBottom: '1px solid #e8e8e8',
                fontWeight: 600, whiteSpace: 'nowrap', color: '#333',
              }}>
                {h.replace(/\*\*/g, '')}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, ri) => (
            <tr key={ri} style={{ background: ri % 2 === 0 ? '#fff' : '#fafafa' }}>
              {row.map((cell, ci) => {
                const isBold = cell.includes('**');
                const cleanCell = cell.replace(/\*\*/g, '');
                const hasLink = cell.includes('#') && ci === 0;
                return (
                  <td key={ci} style={{
                    padding: '6px 10px', borderBottom: '1px solid #f0f0f0',
                    fontWeight: isBold ? 600 : 400, whiteSpace: 'nowrap',
                  }}>
                    {hasLink ? (
                      <span style={{ color: '#1890ff', cursor: 'pointer' }}>{cleanCell}</span>
                    ) : cleanCell}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export const ChatPage: React.FC = () => {
  const navigate = useNavigate();
  const { messages, sessionId, addMessage, clearMessages, isTyping, setTyping } = useChatStore();
  const [input, setInput] = useState('');
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  useEffect(() => {
    chatApi.getSuggestions().then(setSuggestions).catch(() => {});
  }, []);

  const sendMessage = async (text?: string) => {
    const msg = text || input.trim();
    if (!msg) return;

    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      role: 'user',
      content: msg,
      timestamp: new Date().toISOString(),
    };
    addMessage(userMsg);
    setInput('');
    setTyping(true);

    try {
      const res: ChatResponse = await chatApi.sendMessage(sessionId, msg);
      const assistantMsg: ChatMessage = {
        id: crypto.randomUUID(),
        role: 'assistant',
        content: res.response,
        timestamp: res.timestamp,
        evidence: undefined,
      };
      (assistantMsg as any).responseType = res.responseType;
      (assistantMsg as any).data = res.data;
      (assistantMsg as any).intent = res.intent;
      addMessage(assistantMsg);
    } catch (err) {
      addMessage({
        id: crypto.randomUUID(),
        role: 'assistant',
        content: 'Sorry, I encountered an error processing your request. Please try again.',
        timestamp: new Date().toISOString(),
      });
    } finally {
      setTyping(false);
    }
  };

  const renderMessage = (msg: ChatMessage) => {
    const isUser = msg.role === 'user';
    const responseType = (msg as any).responseType;
    const data = (msg as any).data;
    const intent = (msg as any).intent;

    return (
      <div
        key={msg.id}
        style={{
          display: 'flex',
          gap: 12,
          marginBottom: 20,
          padding: '0 20px',
          maxWidth: 900,
          margin: '0 auto 20px',
        }}
      >
        <div style={{
          width: 32, height: 32, borderRadius: '50%',
          background: isUser ? '#1890ff' : '#f0f0f0',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          flexShrink: 0, marginTop: 2,
        }}>
          {isUser
            ? <UserOutlined style={{ color: '#fff', fontSize: 14 }} />
            : <RobotOutlined style={{ color: '#666', fontSize: 14 }} />
          }
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 12, color: '#999', marginBottom: 4 }}>
            {isUser ? 'You' : 'KSP AI Assistant'}
          </div>
          <div style={{
            background: isUser ? '#e6f7ff' : '#fff',
            border: isUser ? 'none' : '1px solid #f0f0f0',
            borderRadius: 10,
            padding: '10px 14px',
            boxShadow: isUser ? 'none' : '0 1px 2px rgba(0,0,0,0.04)',
          }}>
            {isUser ? (
              <div style={{ fontSize: 14 }}>{msg.content}</div>
            ) : (
              <div style={{ fontSize: 14, lineHeight: 1.6 }}>
                {renderMarkdown(msg.content, navigate)}
              </div>
            )}
          </div>
          {!isUser && responseType === 'report' && data?.caseId && (
            <Button
              type="primary"
              size="small"
              icon={<DownloadOutlined />}
              style={{ marginTop: 8, background: '#8B0000', borderColor: '#8B0000' }}
              onClick={() => navigate(`/incidents/${data.caseId}`)}
            >
              View Case & Download FIR
            </Button>
          )}
          {!isUser && intent === 'case_detail' && data?.case_master_id && (
            <Button
              type="link"
              size="small"
              icon={<LinkOutlined />}
              style={{ marginTop: 4, padding: 0 }}
              onClick={() => navigate(`/incidents/${data.case_master_id}`)}
            >
              Open Case Detail Page
            </Button>
          )}
        </div>
      </div>
    );
  };

  const showWelcome = messages.length === 0;

  return (
    <div style={{
      height: 'calc(100vh - 64px)',
      display: 'flex',
      flexDirection: 'column',
      background: '#f7f7f8',
    }}>
      <div style={{ flex: 1, overflowY: 'auto', paddingTop: 20, paddingBottom: 100 }}>
        {showWelcome && (
          <div style={{ maxWidth: 700, margin: '0 auto', padding: '40px 20px', textAlign: 'center' }}>
            <div style={{
              width: 64, height: 64, borderRadius: '50%',
              background: 'linear-gradient(135deg, #1890ff, #722ed1)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              margin: '0 auto 20px',
            }}>
              <RobotOutlined style={{ color: '#fff', fontSize: 28 }} />
            </div>
            <h2 style={{ margin: '0 0 8px', fontSize: 24, fontWeight: 600, color: '#1a1a1a' }}>
              KSP Crime Analytics Assistant
            </h2>
            <p style={{ color: '#666', fontSize: 14, margin: '0 0 32px' }}>
              Ask me anything about cases, criminals, financials, or reports
            </p>

            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: 12,
              textAlign: 'left',
            }}>
              {suggestions.map((s, i) => (
                <button
                  key={i}
                  onClick={() => sendMessage(s.text)}
                  style={{
                    display: 'flex', alignItems: 'center', gap: 12,
                    padding: '12px 16px', borderRadius: 10,
                    border: '1px solid #e8e8e8', background: '#fff',
                    cursor: 'pointer', transition: 'all 0.2s',
                    textAlign: 'left',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.borderColor = '#1890ff';
                    e.currentTarget.style.boxShadow = '0 2px 8px rgba(24,144,255,0.15)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.borderColor = '#e8e8e8';
                    e.currentTarget.style.boxShadow = 'none';
                  }}
                >
                  <div style={{
                    width: 36, height: 36, borderRadius: 8,
                    background: `${CATEGORY_COLORS[s.category] || '#1890ff'}15`,
                    color: CATEGORY_COLORS[s.category] || '#1890ff',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 16, flexShrink: 0,
                  }}>
                    {ICON_MAP[s.icon] || <SearchOutlined />}
                  </div>
                  <div>
                    <div style={{ fontSize: 13, fontWeight: 500, color: '#333' }}>{s.text}</div>
                    <div style={{ fontSize: 11, color: '#999', marginTop: 2 }}>
                      <Tag color={CATEGORY_COLORS[s.category]} style={{ fontSize: 10, lineHeight: '14px', padding: '0 4px', margin: 0 }}>
                        {s.category}
                      </Tag>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}

        {messages.map(renderMessage)}

        {isTyping && (
          <div style={{ maxWidth: 900, margin: '0 auto 20px', padding: '0 20px' }}>
            <div style={{ display: 'flex', gap: 12 }}>
              <div style={{
                width: 32, height: 32, borderRadius: '50%',
                background: '#f0f0f0', display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0,
              }}>
                <RobotOutlined style={{ color: '#666', fontSize: 14 }} />
              </div>
              <div style={{
                background: '#fff', border: '1px solid #f0f0f0',
                borderRadius: 10, padding: '12px 16px',
                display: 'flex', alignItems: 'center', gap: 8,
              }}>
                <Spin size="small" />
                <span style={{ color: '#999', fontSize: 13 }}>Analyzing your request...</span>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <div style={{
        position: 'fixed', bottom: 0, left: 0, right: 0,
        background: '#f7f7f8',
        borderTop: '1px solid #e8e8e8',
        padding: '12px 20px',
        display: 'flex', justifyContent: 'center',
      }}>
        <div style={{
          maxWidth: 860, width: '100%',
          display: 'flex', gap: 8,
          background: '#fff',
          border: '1px solid #d9d9d9',
          borderRadius: 12,
          padding: '4px 4px 4px 16px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
          alignItems: 'center',
        }}>
          <input
            ref={inputRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && sendMessage()}
            placeholder="Ask about cases, statistics, reports..."
            style={{
              flex: 1, border: 'none', outline: 'none',
              fontSize: 14, padding: '8px 0', background: 'transparent',
              fontFamily: 'inherit',
            }}
            disabled={isTyping}
          />
          {messages.length > 0 && (
            <Button
              type="text"
              icon={<ClearOutlined />}
              onClick={clearMessages}
              size="small"
              style={{ color: '#999' }}
              title="Clear conversation"
            />
          )}
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={() => sendMessage()}
            disabled={!input.trim() || isTyping}
            style={{
              borderRadius: 8,
              height: 36,
              background: input.trim() ? '#1890ff' : '#d9d9d9',
            }}
          />
        </div>
      </div>
    </div>
  );
};

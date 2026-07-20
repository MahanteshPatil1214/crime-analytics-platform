import React from 'react';
import { Card, Empty } from 'antd';

export const PersonPage: React.FC = () => {
  return (
    <div style={{ padding: 24 }}>
      <Card bordered={false}>
        <Empty description="Person data is now part of Case Details. Navigate to a case to view persons involved." />
      </Card>
    </div>
  );
};

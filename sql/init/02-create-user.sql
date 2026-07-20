CREATE USER crime_analytics_reader WITH PASSWORD 'reader123';
GRANT CONNECT ON DATABASE crime_analytics TO crime_analytics_reader;
GRANT USAGE ON SCHEMA public TO crime_analytics_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO crime_analytics_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO crime_analytics_reader;

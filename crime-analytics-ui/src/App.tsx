import { AppRouter } from './routes/AppRouter';

function App() {
  return (
    <>
      <AppRouter />
      <div
        style={{
          position: 'fixed',
          bottom: 0,
          left: 0,
          right: 0,
          zIndex: 9999,
          background: 'rgba(0, 21, 41, 0.85)',
          backdropFilter: 'blur(8px)',
          color: 'rgba(255,255,255,0.7)',
          fontSize: 11,
          padding: '6px 16px',
          textAlign: 'center',
          lineHeight: 1.4,
          borderTop: '1px solid rgba(255,255,255,0.08)',
        }}
      >
        This is a fictional demonstration project. All names, places, institutions, case numbers, and data are entirely fictional and do not represent any real persons, agencies, or events.
      </div>
    </>
  );
}

export default App;

import { useCallback, useEffect, useState } from 'react'
import type { Session } from './generated/openapi'
import { AppShell } from './app/AppShell'
import { Button } from './components/Button'
import { LoginPage } from './features/auth/LoginPage'
import { ApiError, getSession, signOut } from './lib/api/client'
import './App.css'

type SessionState =
  | { status: 'loading' }
  | { status: 'anonymous' }
  | { status: 'authenticated'; session: Session }
  | { status: 'error' }

function App() {
  const [sessionState, setSessionState] = useState<SessionState>({ status: 'loading' })
  const [attempt, setAttempt] = useState(0)
  const [signingOut, setSigningOut] = useState(false)
  const [signOutError, setSignOutError] = useState(false)

  useEffect(() => {
    let active = true
    getSession().then(
      (session) => {
        if (active) setSessionState({ status: 'authenticated', session })
      },
      (error: unknown) => {
        if (active) {
          setSessionState(
            error instanceof ApiError && error.status === 401
              ? { status: 'anonymous' }
              : { status: 'error' },
          )
        }
      },
    )
    return () => { active = false }
  }, [attempt])

  const handleSignOut = useCallback(async () => {
    setSigningOut(true)
    setSignOutError(false)
    try {
      await signOut()
      window.history.replaceState(null, '', '/')
      setSessionState({ status: 'anonymous' })
    } catch {
      setSignOutError(true)
    } finally {
      setSigningOut(false)
    }
  }, [])

  if (sessionState.status === 'loading') {
    return <main className="centered-page"><p role="status">Sitzung wird geladen ...</p></main>
  }

  if (sessionState.status === 'error') {
    return (
      <main className="centered-page">
        <div className="card">
          <h1>Verbindung fehlgeschlagen</h1>
          <p role="alert">Die Sitzung konnte nicht geladen werden. Bitte erneut versuchen.</p>
          <Button onClick={() => { setSessionState({ status: 'loading' }); setAttempt((value) => value + 1) }}>
            Erneut versuchen
          </Button>
        </div>
      </main>
    )
  }

  if (sessionState.status === 'anonymous') {
    return <LoginPage onSignedIn={(session) => setSessionState({ status: 'authenticated', session })} />
  }

  return (
    <AppShell
      session={sessionState.session}
      onSignOut={handleSignOut}
      signingOut={signingOut}
      signOutError={signOutError}
    />
  )
}

export default App

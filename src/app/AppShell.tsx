import { useEffect, useState, type MouseEvent } from 'react'
import type { Session } from '../generated/openapi'
import { Button } from '../components/Button'

type AppShellProps = {
  session: Session
  onSignOut: () => void
  signingOut: boolean
  signOutError: boolean
}

type Page = {
  path: string
  label: string
}

const parentPages: Page[] = [
  { path: '/spiele', label: 'Spiele' },
  { path: '/waschen', label: 'Waschen' },
  { path: '/bewirtung', label: 'Bewirtung' },
  { path: '/aufgaben', label: 'Aufgaben' },
]

const roleNames: Record<string, string> = {
  CLUB_ADMIN: 'Vereinsadministration',
  TEAM_MANAGER: 'Mannschaftsverwaltung',
  PARENT_COORDINATOR: 'Elternkoordination',
  PARENT: 'Elternteil',
}

export function AppShell({ session, onSignOut, signingOut, signOutError }: AppShellProps) {
  const [path, setPath] = useState(window.location.pathname)
  const isParent = session.roles.some((role) => role === 'PARENT' || role === 'PARENT_COORDINATOR')
  const isManager = session.roles.some((role) => role === 'CLUB_ADMIN' || role === 'TEAM_MANAGER')
  const pages: Page[] = [
    { path: '/', label: 'Start' },
    ...(isParent ? parentPages : []),
    ...(isManager ? [{ path: '/verwaltung', label: 'Verwaltung' }] : []),
  ]
  const page = pages.find((item) => item.path === path)
  const roles = session.roles.map((role) => roleNames[role] ?? role)

  useEffect(() => {
    const handlePopState = () => setPath(window.location.pathname)
    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  function navigate(event: MouseEvent<HTMLAnchorElement>, destination: string) {
    if (event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return
    event.preventDefault()
    if (path !== destination) {
      window.history.pushState(null, '', destination)
      setPath(destination)
    }
  }

  return (
    <div className="app-shell">
      <a className="skip-link" href="#main-content">Zum Inhalt springen</a>
      <header className="site-header">
        <div className="header-inner">
          <div>
            <p className="brand">TeamManager</p>
            <p className="header-context">Mannschaft noch nicht ausgewählt</p>
            <p className="header-context">Rollen: {roles.length ? roles.join(', ') : 'Keine Rolle zugewiesen'}</p>
          </div>
          <div className="account">
            <span>{session.displayName || session.email}</span>
            <Button className="button-secondary" onClick={onSignOut} disabled={signingOut}>
              {signingOut ? 'Abmeldung läuft ...' : 'Abmelden'}
            </Button>
          </div>
        </div>
      </header>
      <nav className="navigation" aria-label="Hauptnavigation">
        <div className="navigation-inner">
          {pages.map((item) => (
            <a key={item.path} href={item.path} onClick={(event) => navigate(event, item.path)}
              aria-current={path === item.path ? 'page' : undefined}>
              {item.label}
            </a>
          ))}
        </div>
      </nav>
      <main className="main-content" id="main-content" tabIndex={-1}>
        {signOutError && <p className="error" role="alert">Das Abmelden ist fehlgeschlagen. Bitte erneut versuchen.</p>}
        {!page ? (
          <section className="card">
            <h1>Seite nicht verfügbar</h1>
            <p>Diese Seite ist nicht verfügbar.</p>
          </section>
        ) : page.path === '/' ? (
          <>
            <h1>Hallo, {session.displayName || session.email}!</h1>
            <p className="intro">Willkommen in deinem Vereinsbereich.</p>
            <section className="card">
              <h2>Dein Arbeitsbereich</h2>
              <p>Die Auswahl einer Mannschaft und weitere Funktionen folgen in späteren Schritten.</p>
            </section>
          </>
        ) : (
          <>
            <h1>{page.label}</h1>
            <section className="card">
              <p>Dieser Bereich ist noch in Entwicklung.</p>
            </section>
          </>
        )}
      </main>
    </div>
  )
}

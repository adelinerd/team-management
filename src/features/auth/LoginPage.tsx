import { useState, type FormEvent } from 'react'
import type { Session } from '../../generated/openapi'
import { Button } from '../../components/Button'
import { ApiError, signIn } from '../../lib/api/client'

type LoginPageProps = {
  onSignedIn: (session: Session) => void
}

export function LoginPage({ onSignedIn }: LoginPageProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [pending, setPending] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setPending(true)
    setError('')
    try {
      const session = await signIn({ email, password })
      setPassword('')
      onSignedIn(session)
    } catch (cause) {
      setError(
        cause instanceof ApiError && cause.status === 401
          ? 'E-Mail-Adresse oder Passwort ist falsch.'
          : 'Die Anmeldung ist fehlgeschlagen. Bitte erneut versuchen.',
      )
    } finally {
      setPending(false)
    }
  }

  return (
    <main className="centered-page">
      <section className="card login-card" aria-labelledby="login-title">
        <p className="eyebrow">TeamManager</p>
        <h1 id="login-title">Anmelden</h1>
        <p>Melde dich an, um deinen Vereinsbereich zu öffnen.</p>
        <form onSubmit={handleSubmit}>
          <label htmlFor="email">E-Mail-Adresse</label>
          <input id="email" type="email" autoComplete="username" required value={email}
            onChange={(event) => setEmail(event.target.value)} />
          <label htmlFor="password">Passwort</label>
          <input id="password" type="password" autoComplete="current-password" required value={password}
            onChange={(event) => setPassword(event.target.value)} />
          {error && <p className="error" role="alert">{error}</p>}
          <Button type="submit" disabled={pending}>{pending ? 'Anmeldung läuft ...' : 'Anmelden'}</Button>
        </form>
      </section>
    </main>
  )
}

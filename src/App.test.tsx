import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import { ApiError, getSession, signIn, signOut } from './lib/api/client'

vi.mock('./lib/api/client', async (importOriginal) => {
  const original = await importOriginal<typeof import('./lib/api/client')>()
  return {
    ...original,
    getSession: vi.fn(),
    signIn: vi.fn(),
    signOut: vi.fn(),
  }
})

const admin = {
  userId: 'user-1',
  email: 'admin@example.test',
  displayName: 'Alex',
  roles: ['CLUB_ADMIN', 'TEAM_MANAGER'],
}

beforeEach(() => {
  window.history.replaceState(null, '', '/')
  vi.resetAllMocks()
})

describe('application shell', () => {
  it('signs in, navigates with role-aware links, and signs out', async () => {
    vi.mocked(getSession).mockRejectedValue(new ApiError('Unauthorized', 401))
    vi.mocked(signIn).mockResolvedValue(admin)
    vi.mocked(signOut).mockResolvedValue()
    const user = userEvent.setup()
    render(<App />)

    await user.type(await screen.findByLabelText('E-Mail-Adresse'), admin.email)
    await user.type(screen.getByLabelText('Passwort'), 'password')
    await user.click(screen.getByRole('button', { name: 'Anmelden' }))

    expect(await screen.findByRole('heading', { name: 'Hallo, Alex!' })).toBeVisible()
    expect(signIn).toHaveBeenCalledWith({ email: admin.email, password: 'password' })
    await user.click(screen.getByRole('link', { name: 'Verwaltung' }))
    expect(window.location.pathname).toBe('/verwaltung')
    expect(screen.getByRole('heading', { name: 'Verwaltung' })).toBeVisible()
    expect(screen.getByRole('link', { name: 'Verwaltung' })).toHaveAttribute('aria-current', 'page')

    await user.click(screen.getByRole('button', { name: 'Abmelden' }))
    expect(await screen.findByRole('heading', { name: 'Anmelden' })).toBeVisible()
    expect(window.location.pathname).toBe('/')
  })

  it('restores a session and follows browser history without exposing administration to other roles', async () => {
    vi.mocked(getSession).mockResolvedValue({ ...admin, roles: ['PARENT'] })
    render(<App />)

    expect(await screen.findByRole('heading', { name: 'Hallo, Alex!' })).toBeVisible()
    expect(screen.queryByRole('link', { name: 'Verwaltung' })).not.toBeInTheDocument()
    window.history.pushState(null, '', '/verwaltung')
    fireEvent.popState(window)
    expect(screen.getByRole('heading', { name: 'Seite nicht verfügbar' })).toBeVisible()
  })

  it('shows a retryable session error rather than treating a server failure as a logout', async () => {
    vi.mocked(getSession)
      .mockRejectedValueOnce(new Error('Network error'))
      .mockResolvedValueOnce(admin)
    const user = userEvent.setup()
    render(<App />)

    expect(await screen.findByRole('alert')).toHaveTextContent('Sitzung')
    await user.click(screen.getByRole('button', { name: 'Erneut versuchen' }))
    expect(await screen.findByRole('heading', { name: 'Hallo, Alex!' })).toBeVisible()
  })

  it('reports sign-in and sign-out errors without losing the current session', async () => {
    vi.mocked(getSession).mockRejectedValue(new ApiError('Unauthorized', 401))
    vi.mocked(signIn)
      .mockRejectedValueOnce(new ApiError('Invalid email or password', 401))
      .mockResolvedValueOnce(admin)
    vi.mocked(signOut).mockRejectedValue(new Error('Network error'))
    const user = userEvent.setup()
    render(<App />)

    await user.type(await screen.findByLabelText('E-Mail-Adresse'), admin.email)
    await user.type(screen.getByLabelText('Passwort'), 'wrong')
    await user.click(screen.getByRole('button', { name: 'Anmelden' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('E-Mail-Adresse oder Passwort')
    await user.click(screen.getByRole('button', { name: 'Anmelden' }))
    await screen.findByRole('heading', { name: 'Hallo, Alex!' })
    await user.click(screen.getByRole('button', { name: 'Abmelden' }))
    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Abmelden'))
    expect(screen.getByRole('heading', { name: 'Hallo, Alex!' })).toBeVisible()
  })
})

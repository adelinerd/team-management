import { beforeEach, describe, expect, it, vi } from 'vitest'

describe('API session client', () => {
  let fetchMock: ReturnType<typeof vi.fn>
  let client: typeof import('./client')

  beforeEach(async () => {
    vi.resetModules()
    fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)
    client = await import('./client')
  })

  it('sends cookies and renews the CSRF token after sign-in', async () => {
    const session = {
      userId: '4a94292c-6ae5-4db4-bf32-a37a6418d2df',
      email: 'manager@example.test',
      roles: ['CLUB_ADMIN'],
    }
    fetchMock
      .mockResolvedValueOnce(jsonResponse({ headerName: 'X-CSRF-TOKEN', token: 'before' }))
      .mockResolvedValueOnce(jsonResponse(session))
      .mockResolvedValueOnce(jsonResponse({ headerName: 'X-CSRF-TOKEN', token: 'after' }))
      .mockResolvedValueOnce(jsonResponse(session))

    await client.signIn({ email: 'manager@example.test', password: 'secret' })
    await client.getSession()

    const [, signInRequest] = fetchMock.mock.calls[1] as [string, RequestInit]
    const [, sessionRequest] = fetchMock.mock.calls[3] as [string, RequestInit]
    expect(signInRequest.credentials).toBe('include')
    expect(new Headers(signInRequest.headers).get('X-CSRF-TOKEN')).toBe('before')
    expect(new Headers(sessionRequest.headers).get('X-CSRF-TOKEN')).toBe('after')
  })

  it('surfaces API errors without retrying a failed mutation', async () => {
    fetchMock
      .mockResolvedValueOnce(jsonResponse({ headerName: 'X-CSRF-TOKEN', token: 'token' }))
      .mockResolvedValueOnce(
        jsonResponse(
          { title: 'Not Found', status: 404, detail: 'Resource not found', correlationId: 'trace' },
          404,
        ),
      )

    await expect(
      client.signIn({ email: 'manager@example.test', password: 'secret' }),
    ).rejects.toMatchObject({
      name: 'ApiError',
      status: 404,
      correlationId: 'trace',
      message: 'Resource not found',
    })
    expect(fetchMock).toHaveBeenCalledTimes(2)
  })
})

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

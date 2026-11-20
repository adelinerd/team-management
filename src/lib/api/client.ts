import {
  Configuration,
  DefaultApi,
  ResponseError,
  type CsrfToken,
  type Session,
  type SignInRequest,
} from '../../generated/openapi'

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly correlationId?: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

let csrfTokenRequest: Promise<CsrfToken> | undefined

const api = new DefaultApi(
  new Configuration({
    basePath: '/api/v1',
    credentials: 'include',
    apiKey: async () => (await getCsrfToken()).token,
  }),
)

function getCsrfToken(): Promise<CsrfToken> {
  if (!csrfTokenRequest) {
    csrfTokenRequest = api.getCsrfToken().catch((error: unknown) => {
      csrfTokenRequest = undefined
      throw error
    })
  }
  return csrfTokenRequest
}

function invalidateCsrfToken(): void {
  csrfTokenRequest = undefined
}

async function handleApiError<T>(request: Promise<T>): Promise<T> {
  try {
    return await request
  } catch (error) {
    if (!(error instanceof ResponseError)) {
      throw error
    }

    const body: unknown = await error.response.clone().json().catch(() => undefined)
    const problem =
      typeof body === 'object' && body !== null
        ? (body as Record<string, unknown>)
        : undefined
    const message =
      typeof problem?.detail === 'string'
        ? problem.detail
        : typeof problem?.title === 'string'
          ? problem.title
          : `Request failed with status ${error.response.status}`

    throw new ApiError(
      message,
      error.response.status,
      typeof problem?.correlationId === 'string'
        ? problem.correlationId
        : undefined,
    )
  }
}

export async function getSession(): Promise<Session> {
  return handleApiError(api.getSession())
}

export async function signIn(credentials: SignInRequest): Promise<Session> {
  const session = await handleApiError(
    api.createSession({ signInRequest: credentials }),
  )
  invalidateCsrfToken()
  await handleApiError(getCsrfToken())
  return session
}

export async function signOut(): Promise<void> {
  await handleApiError(api.deleteSession())
  invalidateCsrfToken()
}

export async function getApiSpecification(): Promise<string> {
  return handleApiError(api.getApiSpecification())
}

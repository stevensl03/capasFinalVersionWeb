export async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, init)
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `Error ${res.status}`)
  }
  if (res.status === 204) return undefined as T
  return res.json()
}

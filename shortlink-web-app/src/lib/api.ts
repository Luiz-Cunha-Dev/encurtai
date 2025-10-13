import { PaginatedResponse } from "@/types";

const API_BASE_URL = "/api";

export async function getLinks(page: number = 0, limit: number = 10): Promise<PaginatedResponse> {
  const response = await fetch(`${API_BASE_URL}/sms?page=${page}&limit=${limit}`);
  if (!response.ok) {
    throw new Error("Failed to fetch links");
  }
  return response.json();
}

export async function createShortLink(mainUrl: string): Promise<{ shortenedUrl: string }> {
  const response = await fetch(`${API_BASE_URL}/sms`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ mainUrl }),
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({ message: "Failed to create short link" }));
    throw new Error(errorData.message || "Failed to create short link");
  }
  return response.json();
}

export async function deleteShortLink(token: string): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/sms/${token}`, {
    method: "DELETE",
  });
  if (response.status !== 204) {
    throw new Error("Failed to delete link");
  }
}
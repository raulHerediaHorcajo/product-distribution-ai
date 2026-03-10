import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ChatResponse, ChatStreamChunk } from '@models/chat.model';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private readonly api = environment.aiServiceUrl;
  private readonly http = inject(HttpClient);

  sendMessage(message: string, sessionId: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.api}/chat`, {
      message,
      session_id: sessionId,
    });
  }

  sendMessageStreaming(
    message: string,
    sessionId: string
  ): Observable<ChatStreamChunk> {
    return new Observable(observer => {
      const url = `${this.api}/chat/stream`;
      fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message, session_id: sessionId }),
      })
        .then(async res => {
          if (!res.ok) {
            observer.error(new Error(res.statusText || 'Stream request failed'));
            return;
          }
          const reader = res.body?.getReader();
          if (!reader) {
            observer.error(new Error('No response body'));
            return;
          }
          const decoder = new TextDecoder();
          let buffer = '';
          try {
            while (true) {
              const { done, value } = await reader.read();
              if (done) break;
              buffer += decoder.decode(value, { stream: true });
              const lines = buffer.split('\n');
              buffer = lines.pop() ?? '';
              for (const line of lines) {
                const trimmed = line.trim();
                if (!trimmed) continue;
                try {
                  const data = JSON.parse(trimmed) as ChatStreamChunk;
                  if (data.error) {
                    observer.error(new Error(data.error));
                    return;
                  }
                  observer.next(data);
                  if (data.done) {
                    observer.complete();
                    return;
                  }
                } catch {
                  // skip malformed line
                }
              }
            }
            if (buffer.trim()) {
              try {
                const data = JSON.parse(buffer.trim()) as ChatStreamChunk;
                observer.next(data);
              } catch {
                // ignore
              }
            }
            observer.complete();
          } catch (err) {
            observer.error(err);
          }
        })
        .catch(err => observer.error(err));
    });
  }
}

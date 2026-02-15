import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '@models/store.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class StoreService {
  private readonly api = `${environment.apiUrl}/stores`;
  private readonly http = inject(HttpClient);

  getAll(): Observable<Store[]> {
    return this.http.get<Store[]>(this.api);
  }
}

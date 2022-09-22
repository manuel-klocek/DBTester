import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DifferenceList } from './interfaces/DifferenceList';

@Injectable({
  providedIn: 'root'
})

export class RequestService {

  constructor(private http: HttpClient) {
  }

  _loading = new BehaviorSubject<boolean>(false);

  callTest(json: JSON): Observable<DifferenceList[]> {
    this._loading.next(true);
    return this.http.post<DifferenceList[]>('http://localhost:8080/test', json)
  }

  endLoading() {
    this._loading.next(false);
  }
}

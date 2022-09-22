import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CompileToJsonService {
  constructor() { }

  createTestJson(query: JSON, want: JSON): JSON {
    return JSON.parse(JSON.stringify({ header: this.createHeader(), query: query, want: want }))
  }

  createHeader(): JSON {
    return JSON.parse(Date.now().toString())
  }
}
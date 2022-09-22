import { Component, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, of } from 'rxjs';
import { CompileToJsonService } from '../compile-to-json.service';
import { DifferenceList } from '../interfaces/DifferenceList';
import { RequestService } from '../request.service';

@Component({
  selector: 'app-query-test',
  templateUrl: './query-test.component.html',
  styleUrls: ['./query-test.component.scss']
})
export class QueryTestComponent implements OnInit {

  constructor(private compileJson: CompileToJsonService,
    private request: RequestService) { }

  showResult$ = new BehaviorSubject<boolean>(false);
  loadingIndicator$ = this.request._loading
  displayedColumns: string[] = ['expected', 'got']
  differenceList$!: DifferenceList[]

  ngOnInit(): void { }

  runTests(queryString: string, wantString: string): void {
    this.showResult$.next(true)
    this.loadingIndicator$.next(true)
    const json = this.compileJson.createTestJson(JSON.parse(queryString), JSON.parse(wantString))
    this.request.callTest(json).pipe(
      catchError(_ => of([]))
    ).subscribe(response => { this.differenceList$ = response; this.loadingIndicator$.next(false) })
  }

  sortList(diff: string[]): DifferenceList[] {
    if (diff.length === 0) return []
    var list: DifferenceList[] = []
    for (let i = 0; i < diff.length; i = i + 2) {
      list.push({ expected: diff[i], got: diff[i + 1] })
    }
    return list
  }
}
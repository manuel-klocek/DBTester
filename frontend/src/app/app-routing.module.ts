import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManipulateDBComponent } from './manipulate-db/manipulate-db.component';
import { ManipulateQuerydbComponent } from './manipulate-querydb/manipulate-querydb.component';
import { QueryTestComponent } from './query-test/query-test.component';
import { SelectionComponent } from './selection/selection.component';

const routes: Routes = [
  { path: '', component: SelectionComponent },
  { path: 'dbmanipulation', component: ManipulateDBComponent },
  { path: 'qdbmanipulation', component: ManipulateQuerydbComponent },
  { path: 'testquery', component: QueryTestComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
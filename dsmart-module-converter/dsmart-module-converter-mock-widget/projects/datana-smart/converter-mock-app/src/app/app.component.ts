import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SelectedMockItem} from "./selected-mock-item";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit {

    selectedMockItem: SelectedMockItem;

    constructor(private http: HttpClient){}

    ngOnInit(){

        // this.http.get('assets/selected-mock-item.json').subscribe((data:SelectedMockItem) => this.selectedMockItem=data);
        // this.http.get('http://localhost:8080/mock').subscribe((data:SelectedMockItem) => this.selectedMockItem=data);
    }
}

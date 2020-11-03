import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MockListItemModel} from "./models/mock-list-item-model";
import {Subject} from "rxjs";
import {ConverterWidgetMockService} from "./converter-widget-mock.service";
import {takeUntil} from "rxjs/operators";
import {CaseEditorComponent} from "./case-editor/case-editor.component";

@Component({
    selector: 'datana-converter-mock-widget',
    templateUrl: './converter-mock-widget.component.html',
    styleUrls: ['./converter-mock-widget.component.css']
})
export class ConverterWidgetMockComponent implements OnInit, OnDestroy {

    @ViewChild(CaseEditorComponent) caseEditor: CaseEditorComponent;

    _unsubscribe = new Subject<void>();

    selectedCaseName: String;
    startedCaseName: String;
    editorVisible: boolean = false;

    mockList: Array<MockListItemModel> = new Array<MockListItemModel>();

    constructor(private service: ConverterWidgetMockService) {
    }

    ngOnInit(): void {
      this.getCaseList();
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }

    setSelectedCase(selectedCaseName) {
        this.selectedCaseName = selectedCaseName;
        if (this.editorVisible) {
            this.caseEditor.caseSelected(selectedCaseName)
        }
    }

    setStartedCase(startedCaseName) {
        this.startedCaseName = startedCaseName;
    }

    newCase(newCaseName) {
      this.getCaseList();
    }

    getCaseList() {
      this.service.getList().pipe(
        takeUntil(this._unsubscribe)
      ).subscribe(data => {
        this.mockList = data?.cases;
      });
    }

    addCase() {
        this.editorVisible = true;
    }
}

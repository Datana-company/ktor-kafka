import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MockListItemModel} from "./models/mock-list-item-model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {CaseEditorComponent} from "./case-editor/case-editor.component";
import {HostService} from "./services/host.service";

@Component({
    selector: 'datana-converter-mock-widget',
    templateUrl: './converter-mock-widget.component.html',
    styleUrls: ['./converter-mock-widget.component.css']
})
export class ConverterWidgetMockComponent implements OnInit, OnDestroy {

    @ViewChild(CaseEditorComponent) caseEditor: CaseEditorComponent;

    private unsubscribe = new Subject<void>();

    selectedCaseName: string;
    startedCaseName: string;
    editorVisible = false;

    mockList: Array<MockListItemModel> = new Array<MockListItemModel>();

    constructor(private service: HostService) {
    }

    ngOnInit(): void {
      this.getCaseList();
    }

    ngOnDestroy(): void {
        this.unsubscribe.next();
        this.unsubscribe.complete();
    }

    setSelectedCase(selectedCaseName): void {
        this.selectedCaseName = selectedCaseName;
        if (this.editorVisible) {
            this.caseEditor.caseSelected(selectedCaseName);
        }
    }

    setStartedCase(startedCaseName): void {
        this.startedCaseName = startedCaseName;
    }

    newCase(newCaseName): void {
      this.getCaseList();
    }

    getCaseList(): void {
      this.service.getList().pipe(
        takeUntil(this.unsubscribe)
      ).subscribe(data => {
        this.mockList = data?.cases;
      });
    }

    addCase(): void {
        this.editorVisible = true;
    }
}

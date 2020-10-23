import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {takeUntil} from "rxjs/operators";
import {CaseEditorService} from "./case-editor.service";
import {Subject} from "rxjs";

@Component({
    selector: 'case-editor-component',
    templateUrl: './case-editor.component.html',
    styleUrls: ['./case-editor.component.css']
})
export class CaseEditorComponent implements OnInit {

    _unsubscribe = new Subject<void>();

    caseEditorForm = this.formBuilder.group({
        timeStart: ['', Validators.required],
        meltNumber: ['', Validators.required],
        steelGrade: [''],
        crewNumber: [''],
        shiftNumber: [''],
        mode: [''],
        devices: this.formBuilder.group({
            irCamera: this.formBuilder.group({
                id: [''],
                name: [''],
                uri: [''],
                type: ['']
            })
        })
    });

    constructor(private formBuilder: FormBuilder, private service: CaseEditorService) {
    }

    loadTestData() {
        this.caseEditorForm.patchValue({
            timeStart: '1603036535000',
            meltNumber: '12_плавка',
            steelGrade: '12Х18Н10Т',
            crewNumber: 'Бр.№7',
            shiftNumber: '2-ая смена',
            mode: 'emulation',
            devices: {
                irCamera: {
                    id: 'Cam#55',
                    name: 'CameraName',
                    uri: 'URI',
                    type: 'file'
                }
            }
        });
    }

    onSubmit() {
        console.log(this.caseEditorForm.value);
        this.service.addCase(this.caseEditorForm.value).pipe(
            takeUntil(this._unsubscribe)
        ).subscribe(data => {
            console.log(data);
        });
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
        this._unsubscribe.next();
        this._unsubscribe.complete();
    }
}

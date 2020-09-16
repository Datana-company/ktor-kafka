import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'instruction',
  templateUrl: './instruction.component.html',
  styleUrls: ['./instruction.component.css']
})
export class InstructionComponent implements OnInit {

  @Input() time: string = '12:45:30'

  @Input() timeout: string = '2'

  constructor() { }

  ngOnInit(): void {
  }

}

import {Component} from 'angular2/core';
import {Dialog} from "./dialog.component";
import {httpPost} from "./http";
import {Response} from "./http";
import {SubmissionType} from "./main";
import {SubmissionService} from "./submission.service";

@Component({
	selector: 'test-modal',
	template: `
	<form id="newResponseDialog" (ngSubmit)="add()">
	<div>Create Response for {{submission.userName}}</div>
	<div class="column">
	<div class="row">
    <textarea rows="3" [value]="responseText" (input)="setValue($event)"></textarea>
    </div>
    <div *ngIf="msg !== null" class="row error">{{msg}}</div>
	</div>
	<div class="row">
	<button class="default" [disabled]="msg !== null">Add</button>
	<button type="button" (click)="close()">Cancel</button>
	</div>
	</form>`
})

export class NewResponse extends Dialog {
	service: SubmissionService;
	submission: SubmissionType;
	responseText: string = '';
	msg: string = 'The response text is required';

	setData(data: any) {
		this.submission = data.submission;
		this.service = data.service;
	}

	setValue(event: any) {
		this.msg = null;
		let value = event.target.value;
		if (value !== '') {
			this.responseText = value;
		}
		else {
			this.msg = 'The response text is required';
		}
	}

	add() {
		httpPost('api/submissions/' + this.submission.id + '/response',
				'text/json', JSON.stringify({text: this.responseText}))
				.then((r: Response) => {
					this.service.reload();
					this.close();
				});
	}
}

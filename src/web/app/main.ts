import {Component, ElementRef, OnInit} from 'angular2/core';
import {bootstrap} from 'angular2/platform/browser'
import {Response} from "./http";
import {httpPost} from "./http";
import {extractJSON} from "./http";
import {httpGet} from "./http";
import {SubmissionComponent} from "./submission.component";
import {DialogLoader} from "./dialog-loader.service";
import {SubmissionService} from "./submission.service";

@Component({
	selector: 'test-app',
	template: `
	<form (ngSubmit)="submit()" class="column">
    <label>User Name</label>
    <input type="text" size="22" [value]="userName" (input)="setUser($event)">
    <label>Input Text</label>
	<textarea rows="3" (input)="setText($event)"></textarea>
    <label>City</label>
    <input type="text" size="22" [value]="city" (input)="setCity($event)">
    <div *ngIf="weather !== null" id="cityContainer" class="column stretch">
    <span class="row">{{weather.name}}, {{weather.sys.country}}</span>
    <span class="row">Latitude: {{weather.coord.lat}}, Longitude: {{weather.coord.lon}}</span>
    <span class="row"><i class="wi wi-day-sunny"></i></span>
    <span class="row">{{getTemp(weather.main.temp)}} \u00b0C</span>
    <span class="row">{{weather.weather[0].main}}</span>
    </div>
    <div *ngIf="msg !== null" class="row error">{{msg}}</div>
    <button class="default" [disabled]="msg !== null ? true : null">Submit</button>
   	</form>
   	<div class="stretch column">
	<label>Submissions</label>
	<test-submission *ngFor="#s of submissionService.submissions" [submission]="s"></test-submission>
   	</div>`,
	directives: [SubmissionComponent],
	providers: [DialogLoader, SubmissionService]
})

export class AppComponent {
	inputText: string = '';
	city: string = '';
	weather: any = null;
	userName: string = '';
	msg: string;

	constructor(private dialogLoader: DialogLoader, elementRef: ElementRef,
		private submissionService: SubmissionService) {
		dialogLoader.rootElement = elementRef;
		this.validate();
	}

	setText(e: any) {
		this.inputText = e.target.value;
		this.validate();
	}
	setCity(e: any) {
		this.city = e.target.value;
		this.validate();
		//lookup weather
		httpGet('http://api.openweathermap.org/data/2.5/weather?q=' +
					this.city + '&appid=237b188eb34eeac0322fae7f9487c8db')
				.then(extractJSON)
				.then(o => {
					if (o.cod == 200 &&
							o.name.toLowerCase() == this.city.toLowerCase()) {
						this.weather = o;
					}
					else {
						this.weather = null;
					}
					console.log(o);
				})
				.catch(error => {
					this.weather = null;
					console.log(error);
				});
	}
	setUser(e: any) {
		this.userName = e.target.value;
		this.validate();
	}
	validate() {
		this.msg = null;
		if (this.userName === '') {
			this.msg = 'The user name is required';
		}
		else if (this.inputText === '') {
			this.msg = 'The input text is required';
		}
		else if (this.city === '') {
			this.msg = 'The city is required';
		}
	}
	submit() {
		httpPost('api/submissions', 'text/json',
				JSON.stringify({
					name: this.userName,
					city: this.city,
					text: this.inputText
				}))
				.then((r: Response) => {
					this.submissionService.reload();
					this.validate();
				});
	}
	getTemp(k: number) {
		return Math.round(k - 273.5);
	}
}

export interface SubmissionType {
	id: number;
	userName: string;
	city: string;
	text: string;
	last_update: number;
	responses: string[];
}

bootstrap(AppComponent);
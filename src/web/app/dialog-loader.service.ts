import {Injectable, DynamicComponentLoader, ElementRef, Type} from 'angular2/core';

@Injectable()
export class DialogLoader {
	rootElement: ElementRef;

	constructor(private dcl: DynamicComponentLoader) {}

	show(type: Type, data: any) {
		this.dcl.loadNextToLocation(type, this.rootElement)
			.then(ref => ref.instance.init(ref, this, data))
	}
}

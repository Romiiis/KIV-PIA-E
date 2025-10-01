import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'length'
})
export class LengthPipe implements PipeTransform {
  transform(value: any[] | null | undefined): number {
    return value ? value.length : 0;
  }

}

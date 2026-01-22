import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { IssueData } from "../interfaces/issue-data";

@Injectable({
  providedIn: 'root',
})
export class IssueService {
    private issuesSubject: BehaviorSubject<IssueData[]>;
  private apiUrl = '/issues';
    constructor(private http: HttpClient){
        this.issuesSubject = new BehaviorSubject<IssueData[]>([]);
    }
}

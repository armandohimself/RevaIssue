export type IssueStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IssuePriority = 'LOW' | 'MEDIUM' | 'HIGH';
export type IssueSeverity = 'MINOR' | 'MAJOR' | 'CRITICAL';

export interface IssueData {
    issueId: string;
    name: string;
    description: string;
    status: IssueStatus;
    priority: IssuePriority;
    severity: IssueSeverity;
}
export interface IssueCreate {
  name: string;
  description: string;
  severity: IssueSeverity;
  priority: IssuePriority;
}

export interface IssueUpdate {
  name: string;
  description: string;
  severity: IssueSeverity;
  priority: IssuePriority;
}
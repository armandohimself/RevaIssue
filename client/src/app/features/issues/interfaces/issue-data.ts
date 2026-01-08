export type IssueStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IssuePriority = 'LOW' | 'MEDIUM' | 'HIGH';
export type IssueSeverity = 'INFORMATIONAL' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface IssueData {
    issueId: string;
    name: string;
    description: string;
    status: IssueStatus;
    priority: IssuePriority;
    severity: IssueSeverity;
    updatedAt: string;
    createdByUserId?: string;
    createdByUserName?: string;
    assignedToUserId?: string;
    assignedToUserName?: string;
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

export interface LogTransaction {
  logId: number;
  message: string;
  actingUser: {
    userId: string;
    userName: string;
  };
  affectedEntityType: string;
  affectedEntityId: string;
  date: string;
}
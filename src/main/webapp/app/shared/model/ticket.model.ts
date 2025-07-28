import dayjs from 'dayjs';
import { ITicketCategory } from 'app/shared/model/ticket-category.model';
import { ITicketPriority } from 'app/shared/model/ticket-priority.model';
import { IUser } from 'app/shared/model/user.model';
import { TicketStatus } from 'app/shared/model/enumerations/ticket-status.model';

export interface ITicket {
  id?: number;
  title?: string;
  description?: string;
  creationDate?: dayjs.Dayjs | null;
  status?: keyof typeof TicketStatus | null;
  category?: ITicketCategory | null;
  priority?: ITicketPriority | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<ITicket> = {};

# wepay


![alt text](https://github.com/mohadp/wepay/blob/master/doc/First%20View.png)
![alt text](https://github.com/mohadp/wepay/blob/master/doc/SEcond%20View.png)


# TO DO

****************************************************************************************************************
CHANGING: Remove user table (refer to contact IDs, normalized telephone)
****************************************************************************************************************
- Remove User, Recurrence, Location table (done - before 11/15/2015)
- Modify the "Create Sample Data" methods (done - before 11/15/2015)
- Make table ID columns to be auto increment (done - before 11/15/2015)

****************************************************************************************************************
SHOWING EXPENSES
****************************************************************************************************************
Types of expenses:
- Payer (2)
- Not Payer (1)
- Simple Message (0)

- Add Current_User column to member; identifies entries that correspond to current user's session.

New Metrics:
- Current User Metric: Identify if current user is a payer:
	+ sum(case when <payer.role> = <payer> then member_curr_user else 0)
- Payers: Aggregation of payers (role = payer)
	Result (String): <userId>,<percentage>;<userId>,<percentage>... etc.
- PeoplePaid: Aggregation of people who paid (role = paid)
	Result (String): <userId>,<percentage>;<userId>,<percentage>... etc.








Next Things for MVP:
- Delete groups
- Finalize expenses design:
	- New custom views showing people paying/with pie chart?
- Ener new expenses
	- Entry as is? 
	- Delete expense
	- Modification of Expenses? Different (anyone can change expense)?
		- maybe a different activity for editing expenses with a Tab Viewer
	- 
- Enter Expenses :
	- Choose category: (see if we can choose category automatically... any type of library to help with it)
	- Choose payers and percentages
	- Choose

	
- Wire notifications and synchronization of data/messages.
- Investigate service binding for "chatting"



****************************************************************************************************************
SYNCHRONIZATION OF DATA WITH CLOUD
****************************************************************************************************************

- Add new columns for global IDs (for Group and Expense); how about payments?
	- "User table" is the contacts provider of the phone (a user is whoever has a normalized phone [that for now has an account with "Taps"]).
	- @ Sync, we need to "convert" all from global to local (Groups, Members, Expenses, Payers)
	- @ for every instance (group, member, etcs...), we need to add a "version ID" (a GUID, for example) to always keep a single version of the same thing (e.g. send info to cloud, cloud saves data, but cloud fails to acknowledge the synch is successful; next time we want to synch, we do not want to save a duplicate of "locally synched" data row.
	- @ Synch, create all groups with local ID (autocreated); keep the global ID for synching purposes...
	- @ Synch, create all expenses with expenses set to local group ID; no need to keep a global ID for synching (new expense, modified, etc.)
		- Every local user may have a different local expense ID; if a user modifies an expense, we need to modify the correct global instance
	- We do not need global "global payment" IDs; they can be identified by <user, expense, group id>, but having a global ID may help in the synching... if not present, add new; add a flag for modified and deleted.
	- Every entity needs its global ID.

Tentative additional columns for synching:
	-Only in cloud:
		- Cloud will have an structure that relates InstallationID and User.
		- INSTALLATION_ID (saved only on the cloud), for every data sent by clients
		- Tentatively, data will be saved as "Entities" Google Datastore version (only key-value pairs) with parent/child relationships. 
			- Local IDs, not saved (since every user will have different local IDs for the same entities). 
			- Cannot rely on global IDs to relate data locally in sqlite; when adding new groups or expenses if offline, we will not have global IDs
			- Local clients will very probably insert data based on relationships
			- What if locally, a new expense is created; @ synch time, expense is created in cloud with its global ID, but client fails to get acknowledgement... @ next synch, we will see a "new" expense in cloud and insert it locally, and recreate the original in the cloud, ending up with a duplicate expense. How to avoid?
				- Locally generated UUIDs for all entities?
				
	-In local DB (and cloud):
		- Global_ID (assigned by cloud)
		- UUID (to know whether certain element is present or not)
		- DELETE  columns 
		- MODIFY flag 
		- LAST_CHANGE (assigned by cloud) - to know when was the last update (cloud time) -> this is the "version"... it tells us what is newer
		- WHO_CHANGED_LAST (in cloud) - to know who changed something (as in an expense, group, etc.)
			
- Modify the "Create Sample Data" methods

- Complete the Add Group....

- Fully design the Add Expense screen

****************************************************************************************************************
CHANGING THE SAMPLE DATA CREATION (Done - before 11/15/2015)
****************************************************************************************************************

IDs are autoassigned by SQLite: just by not including ID in the insert, it is assigned.

1. Create a group //Index for Group 0
2. Create <random> # of members //Record number of members
3. Create <random> # of expenses // refer to group position
4. Create one payment per member // refer to the member positions (1 to [number of members] -> range is (indexOfGroup, indexOfGroup+numberOfMembers]

****************************************************************************************************************
REFACTORING
****************************************************************************************************************
- Move all the ContentProvider calls to a centralized class
- Modify the Provider: Instead of being fully based on URI matchers, make it more generic: if querying particular tables, simply pass on the query, without hardcoding every URI pattern.

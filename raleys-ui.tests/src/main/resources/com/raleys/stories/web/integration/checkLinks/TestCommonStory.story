Meta:
@author lzakharova
@smoke


Scenario: Legacy integration: Check "Home" link.

Given customer is on <checkPageName> Page
When customer does nothing 3 seconds
When customer clicks Home <linkLocation> with javascript
When customer does nothing 3 seconds
Then Legacy Home Page is opened

Examples:
|checkPageName|linkLocation|
|Store Finder|menu item|


Scenario: Legacy integration: Check "Home" link.

Given customer is on <checkPageName> Page
When customer does nothing 3 seconds
When customer clicks Home <linkLocation>
When customer does nothing 3 seconds
Then Legacy Home Page is opened

Examples:
|checkPageName|linkLocation|
|Store Finder| footer link|
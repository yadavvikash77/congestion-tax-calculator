# congestion-tax-calculator
Files That application read from outside of the application...Keep these files in your machine & chan
1] allowedVehicle.json
2] holidayCalendar.json
3] timeSlot.json

1] allowedVehicle.json
=======================
{
	"allowedVehicles": ["Emergency vehicles", "Busses", "Diplomat vehicles", "Motorcycles", "Military vehicles", "Foreign vehicles"]
}

2] holidayCalendar.json
=======================
{
	"january": ["1"],
	"february":"",
	"march": ["28", "29"],
	"april": ["1", "30"],
	"may": ["1", "8", "9"],
	"june": ["5", "6", "21"],
	"july":"",
	"august":"",
	"september":"",
	"october":"",
	"november": ["1"],
	"december": ["24", "25", "26", "31"]	
}

3] timeSlot.json
=================
{
	"Gothenburg": {
		"timeSlot": [{
				"startTime": "06:00:00",
				"endTime": "06:29:00",
				"taxAmount": "8"
			},
			{
				"startTime": "08:30:00",
				"endTime": "14:59:00",
				"taxAmount": "8"
			},
			{
				"startTime": "18:00:00",
				"endTime": "18:29:00",
				"taxAmount": "8"
			},
			{
				"startTime": "06:30:00",
				"endTime": "06:59:00",
				"taxAmount": "13"
			},
			{
				"startTime": "08:00:00",
				"endTime": "08:29:00",
				"taxAmount": "13"
			},
			{
				"startTime": "15:00:00",
				"endTime": "15:29:00",
				"taxAmount": "13"
			},
			{
				"startTime": "17:00:00",
				"endTime": "17:59:00",
				"taxAmount": "13"
			},
			{
				"startTime": "07:00:00",
				"endTime": "07:59:00",
				"taxAmount": "18"
			},
			{
				"startTime": "15:30:00",
				"endTime": "16:59:00",
				"taxAmount": "18"
			}
		],
		"freeSlot":[ {
			"startTime": "18:30:00",
			"endTime": "05:59:00",
			"taxAmount": "0"
		}]
	}
}

# WalkTriggers

## data
+ ### message
    
    This part includes getting and pushing messages
  
  - AlarmService

    This service is used to manage the alarm notification, such as: 9:00a.m. check weather
  
  - MessageReceive
    
    This can get message for other apps and call different actions with action names

+ ### online
    
    This part includes online service manage
  
  - WeatherDao
    
    Manage Weather data
  
  - WeatherService
    
    This service includes weather api, and Weather repository

+ ### sensor
    
    This part manages senors and system info
  
  - SensorService
    
    Sensor manage service and also as userinfo repository, such as gps and wifi
  
  - UserInfoDao
    
    UserInfo includes location information and other user information
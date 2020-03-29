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
    
    Sensor manage service and also as userInfo repository, such as gps and wifi
  
  - UserInfoDao
    
    UserInfo includes location information and other user information
   
## TaskService

   This service includes all the actions


~~framework background~~

    //todo manage triggers, add them easily
    // manage data source
    // manage notification

    //todo other apps has context provider
    // access;

    // config file? data source
    // different data source -> complex
    // more api -> improve
    // different -> don't call trigger

    // more complex notifications (images, buttons...)
    // step histories, less, more -> notification

    // 5 triggers
    // in report -> screen shots -> how the trigger works
    // highlight ->

    // behaviour changes
    // -> different user types -> type of triggers
    // a lot of | often miss targets
    // pick the right notification

    // low power -> save power -> don't check weather
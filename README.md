# Purdue-Vaccination-Registration
CNIT 425 Project: An android application with an assistant app that facilitates the vaccination registration. 

The web view of this [README.md]( https://github.com/AliceSchuberg/Purdue-Vaccination-Registration/blob/main/README.md)

## Motivation
The project was created due to our team's unpleasant experience with previous COVID-19 testing and vaccination registration, which were mainly web-based and sometimes hard to be found. Browser caches are extremely volatile in the mobile environment.

## Introduction
In order to demonstrate this project, we in fact created two applications, [Purdue Vaccine](https://github.com/AliceSchuberg/Purdue-Vaccination-Registration/tree/main/PurdueVaccine) and [ProtectPurdue Admin](https://github.com/AliceSchuberg/Purdue-Vaccination-Registration/tree/main/ProtectPurdueAdmin). 

Both of them are required to complete the application process: user register for a vaccination -> medical staffs fill in vaccine info for user -> single vaccination completed. As demonstrated, this process involves two different entities to perform their task. Although it's possible to build everything into one app, it's best for the data security not to do so as some users may exploit the app to fake their profile.

### Purdue Vaccine
The primary app we developed, and is for regular users, such as students and faculties. This app was initially developed in this repository: [CNIT425_BlackBoardReborn](https://github.com/jiajack001/CNIT425_BlackBoardReborn).

### ProtectPurdue Admin
The assistant app we developed, and is for medical staffs. It provides the medical staffs the access to the information for each regular user, such as the vaccine exp date, the vaccination date, etc.

## Design and Method
The project was created with the Google Firebase platform, which hosts the authentication and the database. The database chosen for this project is the Realtime Database, which is a NoSQL database. The data structure of the database can be found below.

### Root Node
```
purdue-vaccine---cnit-425-default-rtdb     # <root>
    ├── issue                                  # child node of <root>
    │      │── ...                        
    │      └── <uid + time>                         # child node of <issue>     -> represent each report
    ├── location                               # child node of <root>
    │      │── ...                       
    │      └── <location#>                          # child node of <location>  -> represent each location                
    └── user                                   # child node of <root>
           │── ...
           └── <uid>                                # child node of <user>      -> represent each user
```

### \<issue\> Node
```
  issue                                     # <issue> node
    ├── ...                                     
    └── <uid + time>                            # child node of <issue>    -> represent each report
           │── email: "purdue@purdue.edu"          # child node of each report -> represent the email of the user reported.
           │── msg: "Main body of a message"       # child node of each report -> represent the message attribute.
           │── title: "The report title"           # child node of each report -> represent the title attribute.
           └── uid: "[the same uid in the key]"    # child node of each report -> represent the uid of the user reported.
```

### \<location\> Node
```
  issue                                     # <location> node
    ├── ...                                     
    ├── <location4>                             
    └── <location#>                             # child node of <location>    -> represent each location
           │── name: "the location name, such as Co-Rec"     # child node of each location -> represent the place name
           │── address: "the actual address"                 # child node of each location -> represent the actual address
           │── latitude: <latitude in number>                # child node of each location -> represent the latitude.
           │── longitude: <longitude in number>              # child node of each location -> represent the longitude.
           └── time                                          # child node of each location -> represent the time table.
                 │── ...
                 └── <date>                                     # child node of 'time' -> represent the date in yyyy-mm-dd format
                        │── ...
                        └── <time>                                 # child node of each <date> -> represent the time range in 09:00-10:00 format
                               │── Count: <number>                    # child node of each <time> -> represent the # of registered individuals.
                               │── uid: <email>                       # child node of each <time> -> represent the individual registered.
                               └── ...                                # child node of each <time> -> represent the individual registered.
```

### \<user\> Node
```
  user                                     # <user> node
    ├── ...                                     
    ├── <uid>                             
    └── <uid>                                 # child node of <user>    -> represent each user, the node key is the uid of the user
           │── Email:"purdue@purdue.edu"          # child node of each user -> represent the email of the user.
           └── Vaccination                        # child node of each user -> represent the Vaccination Info Section.
                 │── Vaccinated: <boolean>            # child node of 'Vaccination' -> represent the vaccinated status
                 │── VaccineCount: <number>           # child node of 'Vaccination' -> represent the number of vaccines taken
                 │── <Dose#>                          # child node of 'Vaccination' -> represent the info section for each vaccine dose
                 │      │── completed: <boolean>          # child node of <Dose#> -> represent the completion of this vaccine dose
                 │      │── date: <date>                  # child node of <Dose#> -> represent the completion date of this vaccine dose
                 │      │── location: <location#>         # child node of <Dose#> -> represent the completion location of this vaccine dose
                 │      │── time: <time>                  # child node of <Dose#> -> represent the completion time of this vaccine dose
                 │      │── vaccineExpDate: <date>        # child node of <Dose#> -> represent the expiration date of this vaccine dose
                 │      │── vaccineSerial: <alpha#>       # child node of <Dose#> -> represent the serial number of this vaccine dose
                 │      └── vaccineType: <String>         # child node of <Dose#> -> represent the brand type of this vaccine dose
                 └── ...                              # child node of 'Vaccination' -> represent the info section for each vaccine dose
```

## Prerequisites

0. Internet connection are required!!!!

1. Android Studio 4.0.1+ or an Android device with API 24+

## How to use?

### Run in Android Devices

1. Download the apks [here](https://github.com/AliceSchuberg/Purdue-Vaccination-Registration/releases/tag/1.0.425425).

2. Install them into two different devices.

3. Free to explore! 

### Run in Android Emulators

1. Load the two of Android project folders into Android Studio.

2. For each project, under `Build` tab, click `Clean Project`

3. For each project, under `Build` tab, click `Rebuild Project`

4. Run both projects in the emulator.

## API
[Google Firebase](https://firebase.google.com/)

[Google Maps](https://developers.google.com/maps)

[ZXing Android Embedded](https://github.com/journeyapps/zxing-android-embedded)

## Authors

* **Haowen Lei** 
* **Jack Jia**

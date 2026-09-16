# Effective Work Techniques

A personal working-style note (mindset reminders and the techniques that create impact at work) plus a few practical snippets for generating test data with Instancio.

## Dictation link

https://speechnotes.co/dictate/

## Mindset and working style

- You are wrong == I doubt it
- If some blockers in work, then identify the blockers; if possible try to skip for now and do the work and come back to it later.

## Very Important Techniques

- Voice Vibration Work
  - interest towards work in voice vibration
  - zeal and enthusiasm towards work in voice vibration
  - creates more impact than doing real work; vice versa not true
- Messaging Work
- More communication with team (Communication work)
- More surface work, less real work
- Pina pataram lona lotaram work
- Slow and steady on real and deep work

## Dealing with rude people

Keep that some people are rude and mean to you because of their own insecurities and not interested in doing work and staying in company, not because of you. So don't take it personally and react to it. Just ignore them and focus on your work.

## Practical snippets: test data with Instancio

1. Create object and set dummy data for `FullContactDto` for test case.
2. Use Email Draft technique for copying code from phone to laptop.
3. To generate Dummy object using Instancio
4.
   ```xml
   <dependency>
       <groupId>org.instancio</groupId>
       <artifactId>instancio-core</artifactId>
       <version>4.0.0</version>
   </dependency>
   ```

5. please generate entity class in java with remarks as description
6.
   ```java
   Instancio.setDefaultSettings(Settings.create()
           .set(Keys.ARRAY_MIN_LENGTH, 1)
           .set(Keys.ARRAY_MAX_LENGTH, 2)
           .set(Keys.COLLECTION_MIN_SIZE, 1)
           .set(Keys.COLLECTION_MAX_SIZE, 1));
   ```

# InstagramExtractor
Instagram Extractor Download Videos
Android based Instagram url extractor
=======================================================

These are the urls to the Instagram Video files, so you can stream or download them.

* Builds: [![JitPack](https://jitpack.io/v/Ashusolanki/InstagramExtractor.svg)](https://jitpack.io/#Ashusolanki/InstagramExtractor)

## Gradle

To always build from the latest commit with all updates. Add the JitPack repository:

```java
repositories {
    maven { url "https://jitpack.io" }
}
```

And the dependency:

```
dependencies 
 {
      implementation 'com.github.Ashusolanki:InstagramExtractor:0.0.1'
 }
```  

## Usage

```

new InstagramExtractor() 
{
                @Override
                protected void onExtractionComplete(InstagramFile vimeoFile) {
                    //Exrtaction Complete
                }

                @Override
                protected void onExtractionFail(String Error) {
                  //Extraction Fail
                }
 }.Extractor(this.getActivity(), videoURL);


```

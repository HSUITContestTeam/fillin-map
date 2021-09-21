<a href="">
    <img src="https://user-images.githubusercontent.com/67352902/134118053-5c33741c-694a-4008-87d1-a5afe0b979eb.png" align="right" height="90" />
</a>

# Fillin-Map

추억이 담긴 사진과 지도 정보를 저장하고 공유할 수 있는 지도 채우기 어플인 FILLIN MAP
<center>
[![Video Label](http://img.youtube.com/vi/bkH-0xRsxOM/0.jpg)](https://youtu.be/bkH-0xRsxOM)
</center>
<center>
[![Video Label](http://img.youtube.com/vi/R4jrzsnGyE0/0.jpg)](https://youtu.be/R4jrzsnGyE0)
</center>

![image](https://user-images.githubusercontent.com/67352902/134119013-a275e8d3-0e57-4493-8fb6-7aa9c004a032.png)

## Build With

- Kotlin 1.5.21
- Android Studio 11.0
- Firebase



## Installation

- Google Play store : (예정) 아직 미배포 상태
- git clone 후 Android Studio 에서 run
	```shell
	$ git clone https://github.com/HSUITContestTeam/fillin-map.git
	```


## Design

### Information Architecture
[:link: Google 스프레드 시트]
(https://docs.google.com/spreadsheets/d/1Rpa0oGXCNZ_0timZmMJ-p79qE4Y-RbT6AmciDuoZh9w/edit?usp=sharing)

<p align="center">
<img src="https://user-images.githubusercontent.com/67352902/124473825-bdb07800-ddda-11eb-9749-c7befae8fc5e.jpg"></p>

### Prototype

[:link: 카카오 오븐](https://ovenapp.io/view/PR2x8dVfWWEm1j91vHb98Fg22YqnQ8PD/hk2fZ)
<p align="center">
<img src="https://user-images.githubusercontent.com/67352902/134116671-c5743862-693d-4bbc-bf93-7284732ff8c5.png"></p>


## ScreenShots

### 지도 화면
<p align="center">
<img src="https://user-images.githubusercontent.com/67352902/134122409-953b8ded-3d34-4eb0-9b63-bed2c9790f8e.png"></p>

### 프로필/친구목록
<img src="https://user-images.githubusercontent.com/67352902/134123123-2ad4c3c5-6f71-45ef-b318-291d843a37ee.png"></p>


## Commit Message Style
#### 타입

| 태그이름 | 설명                                                  |
| -------- | ----------------------------------------------------- |
| Feat     | 새로운 기능을 추가할 경우                             |
| Fix      | 버그를 고친 경우                                      |
| Design   | CSS 등 사용자 UI 디자인 변경                          |
| Style    | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우 |
| Refactor | 프로덕션 코드 리팩토링                                |
| Comment  | 필요한 주석 추가 및 변경                              |
| Docs     | 문서를 수정한 경우                                    |
| Rename   | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우    |
| Remove   | 파일을 삭제하는 작업만 수행한 경우                    |



#### 이슈

- Board 에서 드래그 해서 닫아도 됨 [여기](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues) 참고
- `commit -m` 으로 닫는 경우 **이슈 키워드**
  - Close, Closes, Closed, Closing, close, closes, closed, closing
  - Fix, Fixes, Fixed, Fixing, fix, fixes, fixed, fixing
  - Resolve, Resolves, Resolved, Resolving, resolve, resolves, resolved, resolving
  - Implement, Implements, Implemented, Implementing, implement, implements, implemented, implementing



#### 메세지

- 영어로 작성하는 경우
  - 첫 글자는 대문자
  - "Fix", "Add", "Change"의 명령어로 시작
- 한글로 작성하는 경우
  - "고침", "추가", "변경"의 명령어로 시작

```html
Feat: 추가 get data api 함수 - [이슈키워드] #이슈번호
```

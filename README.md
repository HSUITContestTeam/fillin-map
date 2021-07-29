# 2021 HSU 공학 경진 대회
 
## 설계

### Information Architecture
[:link: Google 스프레드 시트](https://docs.google.com/spreadsheets/d/1Rpa0oGXCNZ_0timZmMJ-p79qE4Y-RbT6AmciDuoZh9w/edit?usp=sharing)
![a0b5089d-ff4d-4fdb-9bd2-ddb7c05780d1 pdf-0001](https://user-images.githubusercontent.com/67352902/124473825-bdb07800-ddda-11eb-9749-c7befae8fc5e.jpg)

### Prototype
[:link: 카카오 오븐](https://ovenapp.io/view/PR2x8dVfWWEm1j91vHb98Fg22YqnQ8PD/hk2fZ)
<p align="center">
 <img src="https://user-images.githubusercontent.com/67352902/125629145-a6e661f5-33ca-4e63-ac38-57fa4d2cd50a.png" width="40%">
</>

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

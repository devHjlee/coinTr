테스트 목록
20230807
- 데이터저장 : 24시간 지켜보고 데이터 누락시 레디스 추가 ->예상되는 누락 사유는 웹소켓으로 현재가를 받아오는데 거래가 없는 시간에는 웹소켓 데이터가 들어오지 않음 -> 스케줄러추가 
- RSI : 계산값 정확도 올리기



구현 목록(ing)
* 웹소켓 : nv-websocket-client(https://github.com/TakahikoKawasaki/nv-websocket-client)  [okhttp3 였으나 버그로 변경]  
* 알림 : 텔레그램
* 스케줄러 : 쿼츠
* 코인정보 : 업비트
* 코인별 데이터 검증기능

문제점 의문점
* Mybatis 데이터 누락현상 -> 레디스교체
* 일봉 기준으로 지표는 해결됨
* ~~분봉으로 변경시 분 기준 로우 하이 금액을 다른 프로젝트들은 어케 갖고옴? 이걸로인해 안맞는 지표가 생김~~
* 로우 하이 금액이 들어가는 지표 표시

  (ChatGPT)
* STOCHASTIC
* 볼린저밴드
  (https://github.com/jasonlam604/StockTechnicals/tree/master/src/com/jasonlam604/stocktechnicals)
* RSI 
* MACD 
* CCI  : 고가,저가
* ADX  : 고가,저가
* Aroon  : 고가,저가
* ParabolicSar : 고가,저가
* EMA : 확인해라
  
알림기능
* 텔레그램

알림조건
* 이건 블라인드에 물어봐야지

GUI
* 파이썬 애플리케이션을 따로 구성하여 coinTrade 와 http 통신
* 서버상태 표시
* 검색조건식 컨트롤




블라 문의

일봉 지표보는 사람들 정보
 * 어떤 지표, 입력값

분봉 지표 보는 사람들 정보
 * 어떤 지표 , 입력값




차트와 보조지표를 이용해 현물을 하는사람?
구현한 보조지표 :
* STOCHASTIC
* 볼린저밴드
* RSI
* MACD
* CCI
* ADX
* Aroon
* ParabolicSar

더필요한 보조지표
 
자동매매 프로그램들은 알림조건
ex) RSI > 10 and MACD > 0 and CCI > 0

사용자ui 에 차트가 트레이닝뷰 인지?
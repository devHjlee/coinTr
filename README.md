구현 목록(ing)
* 웹소켓 : nv-websocket-client(https://github.com/TakahikoKawasaki/nv-websocket-client)  [okhttp3 였으나 버그로 변경]  
* 알림 : 텔레그램
* 스케줄러 : 쿼츠
* 코인정보 : 업비트

지표
문제점 의문점
* 일봉 기준으로 지표는 해결됨
* 분봉으로 변경시 분 기준 로우 하이 금액을 다른 프로젝트들은 어케 갖고옴? 이걸로인해 안맞는 지표가 생김
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


프로그램 구매해봤던 사람들한테 지표 입력 어떤식으로 하는지?
키움증권 조건검색같은 기능인지?
어떠한 기능들이 있는지?

임시저장
<html>
  <head>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(drawChart);

function drawChart() {
var data = google.visualization.arrayToDataTable([
['Mon', 100, 180, 150, 200], // 저 ,현, 시, 고
['Tue', 31, 38, 55, 66],
['Wed', 50, 55, 77, 80],
['Thu', 77, 77, 66, 50],
['Fri', 68, 66, 22, 15]
// Treat first row as data as well.
], true);

    var options = {
      legend:'none'
    };

    var chart = new google.visualization.CandlestickChart(document.getElementById('chart_div'));

    chart.draw(data, options);
}
</script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>

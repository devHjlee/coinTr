drop table trade_INFO;

CREATE TABLE trade_info (
                            market varchar(255) not null,
                            trade_date varchar(255) not NULL,
                            trade_price double DEFAULT NULL,
                            opening_price double DEFAULT NULL,
                            high_price double DEFAULT NULL,
                            low_price double DEFAULT NULL,
                            cci double DEFAULT NULL,
                            bb_avg double DEFAULT NULL,
                            bb_up double DEFAULT NULL,
                            bb_down double DEFAULT NULL,
                            rsi double DEFAULT NULL,
                            macd double DEFAULT NULL,
                            macd_ema_short double DEFAULT NULL,
                            macd_ema_long double DEFAULT NULL,
                            macd_signal double DEFAULT NULL,
                            macd_signal_histogram double DEFAULT NULL,
                            adx double DEFAULT NULL,
                            p_sar double DEFAULT NULL,
                            aroon_up double DEFAULT NULL,
                            aroon_down double DEFAULT NULL,
                            aroon_oscillator double DEFAULT NULL,
                            fast_k double DEFAULT NULL,
                            fast_d double DEFAULT NULL,
                            slow_k double DEFAULT NULL,
                            slow_d double DEFAULT NULL,
                            acc_trade_price double DEFAULT NULL,
                            acc_trade_volume double DEFAULT NULL,
                            acc_ask_volume double DEFAULT NULL,
                            acc_bid_volume double DEFAULT NULL,
                            PRIMARY KEY (market,trade_date)
) ;

DROP TABLE COIN;

CREATE TABLE COIN (
                        MARKET varchar(255) NOT NULL,
                        KOREAN_NAME varchar(255),
                        PRIMARY KEY (MARKET)
) ;

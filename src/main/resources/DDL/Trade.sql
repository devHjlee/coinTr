DROP TABLE TRADE_INFO;

CREATE TABLE TRADE_INFO (
                            MARKET varchar(255) NOT NULL,
                            TRADE_DATE varchar(255) NOT NULL,
                            TRADE_PRICE double DEFAULT NULL,
                            OPENING_PRICE double DEFAULT NULL,
                            HIGH_PRICE double DEFAULT NULL,
                            LOW_PRICE double DEFAULT NULL,
                            RSI double DEFAULT NULL,
                            MACD double DEFAULT NULL,
                            MACD_EMA_SHORT double DEFAULT NULL,
                            MACD_EMA_LONG double DEFAULT NULL,
                            MACD_SIGNAL double DEFAULT NULL,
                            MACD_SIGNAL_HISTOGRAM double DEFAULT NULL,
                            ACC_TRADE_PRICE double DEFAULT NULL,
                            ACC_TRADE_VOLUME double DEFAULT NULL,
                            ACC_ASK_VOLUME double DEFAULT NULL,
                            ACCBIDVOLUME double DEFAULT NULL,
                            PRIMARY KEY (`market`,`trade_Date`)
) ;

DROP TABLE COIN;

CREATE TABLE COIN (
                        MARKET varchar(255) NOT NULL,
                        KOREAN_NAME varchar(255),
                        PRIMARY KEY (MARKET)
) ;

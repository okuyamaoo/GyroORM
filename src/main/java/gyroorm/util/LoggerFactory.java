package gyroorm.util;

import java.util.logging.*;

import gyroorm.*;

public class LoggerFactory {

	private LoggerFactory() {}


	public static Logger getLogger() {
		Logger logger = null;
		if (GyroORMConfig.userLoggerSetting) {
			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);	
		} else {

			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

			StreamHandler streamHandler = new StreamHandler() {
				{
					setOutputStream(System.out);
					setLevel(GyroORMConfig.LOGGER_LEVEL);
				}
			};
			logger.addHandler(streamHandler);
			logger.setUseParentHandlers(false);
			logger.setLevel(GyroORMConfig.LOGGER_LEVEL);
		}
		return logger;
	}

	public static Logger getLogger(String loggerName) {
		Logger logger = null;
		if (GyroORMConfig.userLoggerSetting) {
			logger = Logger.getLogger(loggerName);	
		} else {

				logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

			StreamHandler streamHandler = new StreamHandler() {
					{
						setOutputStream(System.out);
						setLevel(GyroORMConfig.LOGGER_LEVEL);
					}
				};

				logger.addHandler(streamHandler);
				logger.setUseParentHandlers(false);
				logger.setLevel(GyroORMConfig.LOGGER_LEVEL);
			}
		return logger;
	}
}
FROM mariadb
MAINTAINER hegaoye <hegaoye@gmail.com>
ENV WORK_PATH /usr/local/work
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d
ENV FILE_0 ai_code.sql
ENV INSTALL_DATA_SHELL install_data.sh
RUN mkdir -p $WORK_PATH
COPY ./$FILE_0 $WORK_PATH/
COPY ./$INSTALL_DATA_SHELL $AUTO_RUN_DIR/
RUN chmod a+x $AUTO_RUN_DIR/$INSTALL_DATA_SHELL
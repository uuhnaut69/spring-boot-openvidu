FROM node:latest

LABEL maintainer="@uuhnaut69"

ENV APP /src

RUN mkdir ${APP}
WORKDIR ${APP}
ADD . ${APP}

RUN npm install
RUN npm run build

ENV NUXT_HOST=0.0.0.0
ENV NUXT_PORT=3000
EXPOSE 3000

CMD [ "npm", "start" ]
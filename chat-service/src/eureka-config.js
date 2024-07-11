const Eureka = require('eureka-js-client').Eureka;

// Example configuration for registering with Eureka server
const eurekaClient = new Eureka({
  instance: {
    app: 'nodejs-service', 
    instanceId: 'nodejs-service-instance', 
    hostName: 'localhost', 
    ipAddr: '127.0.0.1',
    statusPageUrl: 'http://localhost:8084/info', 
    healthCheckUrl: 'http://localhost:8084/health',
    port: {
      '$': 8084, 
      '@enabled': 'true',
    },
    vipAddress: 'nodejs-service', 
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn', 
    },
    metadata: {
      'nodejs-version': process.version, 
    },
  },
  eureka: {
    host: 'fitness-eureka-service.default.svc.cluster.local',
    // host: 'localhost', // Replace with your Eureka server's hostname
    port: 8761, 
    servicePath: '/eureka/apps/',
  },
});

module.exports = {
  eurekaClient,
};
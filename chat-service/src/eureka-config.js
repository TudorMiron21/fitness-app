const Eureka = require('eureka-js-client').Eureka;

// Example configuration for registering with Eureka server
const eurekaClient = new Eureka({
  instance: {
    app: 'nodejs-service', // Your service name
    instanceId: 'nodejs-service-instance', // Optional: unique instance id
    hostName: 'localhost', // Hostname where your service will be running
    ipAddr: '127.0.0.1', // IP Address where your service will be running
    statusPageUrl: 'http://localhost:8084/info', // URL path for Eureka to fetch statuses
    healthCheckUrl: 'http://localhost:8084/health', // URL path for Eureka to fetch health checks
    port: {
      '$': 8084, // Port number where your service will be running
      '@enabled': 'true',
    },
    vipAddress: 'nodejs-service', // The same as your app name by convention
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn', // Use 'MyOwn', unless you're running in AWS (which would be 'Amazon')
    },
    // Add any additional metadata as needed
    metadata: {
      'nodejs-version': process.version, // Example of additional metadata
    },
  },
  eureka: {
    // Eureka server's host & port
    host: fitness-eureka-service,
    // host: 'localhost', // Replace with your Eureka server's hostname
    port: 8761, // Replace with your Eureka server's port
    servicePath: '/eureka/apps/',
  },
});

module.exports = {
  eurekaClient,
};
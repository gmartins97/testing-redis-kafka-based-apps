const exec = require('child_process').exec;
const spawn = require('child_process').spawn;

(function() {
    console.log('Starting containers...');
    exec('docker-compose up -d', (err1, stdout1, stderr1) => {
         if (err1) {
            console.error(`docker-compose up -d error: ${err1}`);
            console.error(stderr1);
            process.exit(1);
         }
         console.log('\nContainers started');

         console.log('\nStarting application...');
         const appProcess = spawn('mvn', ['quarkus:dev'], {detached: true, cwd: __dirname});
         console.log('\nApplication started');

         setTimeout(() => {
                console.log('\nRunning newman tests...');
               exec('newman run fibonacci.postman_collection.json -r cli,html', (err2, stdout2, stderr2) => {
                    if (err2) {
                        console.error(`newman error: ${err2}`);
                        console.error(stderr2);
                        process.exit(1);
                    }
                    console.log(stdout2);

                    console.log('\nKilling application...');
                    if (appProcess.kill()) {
                        console.log('\nApplication killed');
                       console.log('\nKilling containers...');
                       exec('docker-compose down', (err3, stdout3, stderr3) => {
                            if (err3) {
                               console.error(`docker-compose down error: ${err3}`);
                               console.error(stderr3);
                               process.exit(1);
                           }

                           console.log('\nContainers killed');
                           process.exit(0);
                       });
                    }
               });
         }, 7000);

         /*appProcess.on('close', (code, signal) => {

       });*/
    });
})();
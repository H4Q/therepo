var startApp = angular.module('h4sApp', []);


startApp.controller('h4sCtrl', function ($scope, $http, $log) {
		
	
	$scope.generateChart = function () {
    	$http({
        	method: 'GET', 
        	url: '/ui/' + $scope.input	
    	}).
        success(function (data, status, headers, config) {
        	$scope.chart(data);
        }).
        error(function (data, status, headers, config) {
            $log.error(status);
        });
	};
	
	$scope.chart = function (chartData) {
		
		var xs = chartData.x;
		xs.unshift('A');
		var ys = chartData.y;
		ys.unshift('Andel röster riksdagsvalet 2014');
		
		c3.generate({
			bindto : '#chart',
			data : {
				x: 'A',
				columns : [chartData.x, chartData.y]
			},
		    axis: {
		        x: {
		            label: 'Medelinkomst, avvikelse från riksmedel (tkr)',
		            tick: {
		                fit: false
		            }
		        },
		        y: {
		            label: 'Andel röster i riksdagsvalet (%)'
		        }
		    }
		});
	};
	
	
//		$scope.chart = function () {
//			c3.generate({
//				bindto : '#chart',
//				data : {
//					x : 'Procent röster',
//					columns : [
//							[ 'Procent röster', 50, 20, 10, 40, 15, 25 ],
//							[ 'Avvikelse från medelinkomst', 30, 200, 100, 400, 150,
//									250 ] ]
//				}
//			});
//		};

});

    


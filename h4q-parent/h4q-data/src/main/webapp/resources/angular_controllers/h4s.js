var startApp = angular.module('h4sApp', []);


startApp.controller('h4sCtrl', function ($scope, $http, $log) {
		
	
	$scope.generateChart = function () {
    	$http({
        	method: 'GET', 
        	url: '/ui/parties/' + $scope.input	
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
		ys.unshift("Percentage of votes in the 2014 general election (%)");
		
		c3.generate({
			bindto : '#chart',
			data : {
				x: 'A',
				columns : [chartData.x, chartData.y],
				type: 'scatter'
			},
		    axis: {
		        x: {
		            label: "Deviation from Swedish average income (tkr)",
		            tick: {
		                fit: false
		            }
		        },
		        y: {
		            label: 'Percentage of votes in the 2014 general election (%)'
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

    


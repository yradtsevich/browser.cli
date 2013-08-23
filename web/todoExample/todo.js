function TodoCtrl($scope) {
	$scope.todos = [ {
		text : 'learn angular',
		done : true
	}, {
		text : 'build an angular app',
		done : false
	} ];

	$scope.addTodo = function() {
		$scope.todos.push({
			text : $scope.todoText,
			done : false
		});
		$scope.todoText = '';
	};

	$scope.remaining = function() {
		var count = 0;
		angular.forEach($scope.todos, function(todo) {
			count += todo.done ? 0 : 1;
		});
		return count;
	};

	$scope.archive = function() {
		var oldTodos = $scope.todos;
		$scope.todos = [];
		angular.forEach(oldTodos, function(todo) {
			if (!todo.done)
				$scope.todos.push(todo);
		});
	};
}

function TodoCtrl2($scope) {
	$scope.todos2 = [ {
		text : 'learn angular2',
		done : true
	}, {
		text : 'build an angular app2',
		done : false
	} ];

	$scope.addTodo2 = function(dumbArg0, dumbArg2) {
		$scope.todos2.push({
			text : $scope.todoText,
			done : false
		});
		$scope.todoText = '';
	};

	$scope.remaining2 = function() {
		var count = 0;
		angular.forEach($scope.todos2, function(todo) {
			count += todo.done ? 0 : 1;
		});
		return count;
	};

	$scope.archive2 = function() {
		var oldTodos = $scope.todos2;
		$scope.todos2 = [];
		angular.forEach(oldTodos, function(todo) {
			if (!todo.done)
				$scope.todos2.push(todo);
		});
	};
}

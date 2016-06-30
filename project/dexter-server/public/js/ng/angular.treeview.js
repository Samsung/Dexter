/*
	@license Angular Treeview version 0.1.6
	ⓒ 2013 AHN JAE-HA http://github.com/eu81273/angular.treeview
	License: MIT
*/
(function ( angular ) {
	'use strict';

	angular.module( 'angularTreeview', [] ).directive( 'treeModel', ['$compile', function( $compile ) {
		return {
			restrict: 'A',
			link: function ( scope, element, attrs ) {
				var treeId = attrs.treeId;
				var treeModel = attrs.treeModel;
				var treeFilter = attrs.treeFilter;
				var nodeId = attrs.nodeId || 'id';
				var nodeLabel = attrs.nodeLabel || 'label';
				var nodeChildren = attrs.nodeChildren || 'children';
				var template =
					'<ul>' +
						'<li data-ng-repeat="node in ' + treeModel + ' | filter:' + treeFilter + '">' +
							'<i class="glyphicon" data-ng-class="' + treeId + '.getClass(node)" data-ng-click="' + treeId + '.selectNodeHead(node)"></i>' +
							'<span data-ng-class="node.selected" data-ng-click="' + treeId + '.selectNodeLabel(node)" title="{{node.' + nodeLabel + '}}">{{node.' + nodeLabel + '}}</span>' +
							'<div data-ng-hide="node.collapsed" data-tree-id="' + treeId + '" data-tree-model="node.' + nodeChildren + '" data-node-id=' + nodeId + ' data-node-label=' + nodeLabel + ' data-node-children=' + nodeChildren + '></div>' +
						'</li>' +
					'</ul>';


				//check tree id, tree model
				if( treeId && treeModel ) {

					//root node
					if( attrs.angularTreeview ) {
					
						scope[treeId] = scope[treeId] || {};

						scope[treeId].selectNodeLabel = scope[treeId].selectNodeLabel || function( selectedNode ){
							if( scope[treeId].selectedNode && scope[treeId].selectedNode.selected ) {
								scope[treeId].selectedNode.selected = undefined;
							}

							selectedNode.selected = 'selected';
							scope[treeId].currentNode = selectedNode;
							scope[treeId].selectedNode = selectedNode;

							if (!selectedNode.hasContent && selectedNode.children.length > 0)
								selectedNode.collapsed = !selectedNode.collapsed;
						};

						scope[treeId].selectNodeHead = scope[treeId].selectNodeHead || function( selectedNode ){
							scope[treeId].currentNode = selectedNode;

							if (selectedNode.children.length > 0)
								selectedNode.collapsed = !selectedNode.collapsed;
						};

						scope[treeId].getClass = scope[treeId].getClass || function ( selectedNode) {
							var className;
							if (selectedNode.type === 'project') {
								className = 'glyphicon-chevron-down';
								if (selectedNode.children.length &&	!selectedNode.collapsed) {
									className = 'glyphicon-chevron-up';
								}
							} else if (selectedNode.type === 'file') {
								className = 'glyphicon-file';
							} else if (selectedNode.type === 'module'){
								className = 'glyphicon-folder-close';
								if (selectedNode.children.length !== 0 && !selectedNode.collapsed) {
									className = 'glyphicon-folder-open';
								} 
							}
								return className;
						};
					}

					//Rendering template.
					element.html('').append( $compile( template )( scope ) );
				}
			}
		};
	}]);
})( angular );

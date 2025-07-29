const Nav = () => {
	return (
		<nav className="bg-gray-800 border-b border-gray-700 px-6 py-4">
			<div className="max-w-6xl mx-auto flex items-center justify-between">
				<div className="flex items-center space-x-3">
					<div className="w-8 h-8 bg-purple-600 rounded-lg flex items-center justify-center">
						<span className="text-white font-bold text-sm">HS</span>
					</div>
					<span className="text-white font-semibold text-lg">HTTP Server</span>
				</div>

				<ul className="flex items-center space-x-8">
					<li>
						<a
							href="/"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							Home
						</a>
					</li>
					<li>
						<a
							href="/test"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							Test
						</a>
					</li>
					<li>
						<a
							href="/docs"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							Docs
						</a>
					</li>
					<li>
						<a
							href="/about"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							About
						</a>
					</li>
				</ul>
			</div>
		</nav>
	);
};

export default Nav;

const Footer = () => {
	return (
		<footer className="bg-gray-800 border-t border-gray-700 py-8">
			<div className="max-w-6xl mx-auto px-6">
				<div className="grid grid-cols-1 md:grid-cols-3 gap-8">
					<div className="space-y-4">
						<div className="flex items-center space-x-3">
							<div className="w-6 h-6 bg-purple-600 rounded flex items-center justify-center">
								<span className="text-white font-bold text-xs">HS</span>
							</div>
							<span className="text-white font-semibold">HTTP Server</span>
						</div>
						<p className="text-gray-400 text-sm">
							A custom Java HTTP server implementation serving React
							applications with modern web technologies.
						</p>
					</div>

					<div className="space-y-4">
						<h3 className="text-white font-semibold">Quick Links</h3>
						<ul className="space-y-2">
							<li>
								<a
									href="/"
									className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
								>
									Home
								</a>
							</li>
							<li>
								<a
									href="/test"
									className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
								>
									Test Page
								</a>
							</li>
							<li>
								<a
									href="/docs"
									className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
								>
									Documentation
								</a>
							</li>
							<li>
								<a
									href="/about"
									className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
								>
									About
								</a>
							</li>
						</ul>
					</div>

					<div className="space-y-4">
						<h3 className="text-white font-semibold">Server Info</h3>
						<div className="space-y-2">
							<div className="flex items-center space-x-2">
								<div className="w-2 h-2 bg-green-400 rounded-full"></div>
								<span className="text-gray-400 text-sm">Status: Online</span>
							</div>
							<div className="text-gray-400 text-sm">
								Built with Java & React
							</div>
							<div className="text-gray-400 text-sm">
								Local Development Server
							</div>
						</div>
					</div>
				</div>

				<div className="border-t border-gray-700 mt-8 pt-6 text-center">
					<p className="text-gray-400 text-sm">
						© 2025 Custom HTTP Server. All rights reserved.
					</p>
				</div>
			</div>
		</footer>
	);
};

export default Footer;

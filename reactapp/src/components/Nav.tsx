import { Link } from "react-router-dom";

const Nav = () => {
	return (
		<nav className="bg-gray-800 border-b border-gray-700 px-6 py-4">
			<div className="max-w-6xl mx-auto flex items-center justify-between">
				<div className="flex items-center space-x-3">
					<span className="text-white font-semibold text-lg">HTTP Server</span>
				</div>

				<ul className="flex items-center space-x-8">
					<li>
						<Link
							to="/"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							Home
						</Link>
					</li>
					<li>
						<Link
							to="/test"
							className="text-gray-300 hover:text-white transition-colors duration-200 font-medium"
						>
							Test Subject API
						</Link>
					</li>
				</ul>
			</div>
		</nav>
	);
};

export default Nav;
